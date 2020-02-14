package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.OverUnderParserLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.OverUnder;
import com.zylex.livebetbot.service.driver.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Service
public class OverUnderParser {

    private static final OverUnderParserLogger logger = new OverUnderParserLogger();

    private DriverManager driverManager;

    @Autowired
    public OverUnderParser(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    public Set<Game> parse(Set<Game> games) {
        if (games.isEmpty()) {
            logger.startLogMessage(LogType.NO_GAMES, 0);
            return games;
        }
        logger.startLogMessage(LogType.OKAY, games.size());
        games.forEach(this::parseSingleGame);
        return games;
    }

    private void parseSingleGame(Game game) {
        try {
            driverManager.getDriver().navigate().to("http://ballchockdee.com" + game.getLink());
            List<OverUnder> overUnderList = findOverUnder();
            game.setOverUnderList(overUnderList);
            overUnderList.forEach(overUnder -> overUnder.setGame(game));
            logger.logGame(LogType.OKAY);
        } catch (TimeoutException e) {
            logger.logGame(LogType.ERROR);
        }
    }

    private List<OverUnder> findOverUnder() {
        driverManager.waitElement(By::className, "MarketT");
        Document document = Jsoup.parse(driverManager.getDriver().getPageSource());
        Elements marketElements = document.select("div.MarketT");
        List<Element> overUnderMarketElements = marketElements.stream()
                .filter(market -> market.select("div.SubHead > span").text().contains("Over Under"))
                .collect(Collectors.toList());
        List<OverUnder> overUnderList = new ArrayList<>();
        for (Element marketElement : overUnderMarketElements) {
            Elements overUnderElements = marketElement.select("table > tbody > tr");
            overUnderElements.forEach(overUnderElement -> extractOverUnder(overUnderList, overUnderElement));
        }
        return overUnderList;
    }

    private void extractOverUnder(List<OverUnder> overUnderList, Element overUnderElement) {
        if (overUnderElement.className().equals("OddsClosed")) {
            return;
        }
        double overSize = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabL > span.OddsM").text());
        double overCoefficient = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabL > span.OddsR").text());
        overUnderList.add(new OverUnder(OverUnder.Type.OVER.toString(), overSize, overCoefficient));
        double underSize = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsM").text());
        double underCoefficient = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsR").text());
        overUnderList.add(new OverUnder(OverUnder.Type.UNDER.toString(), underSize, underCoefficient));
    }
}
