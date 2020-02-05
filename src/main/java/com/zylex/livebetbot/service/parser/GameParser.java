package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.GameParserLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.OverUnder;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.util.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Service
public class GameParser {

    private WebDriver driver;

    private DriverManager driverManager;

    private WebDriverUtil webDriverUtil;

    private GameParserLogger logger = new GameParserLogger();

    @Autowired
    public GameParser(DriverManager driverManager, WebDriverUtil webDriverUtil) {
        this.driverManager = driverManager;
        this.webDriverUtil = webDriverUtil;
    }

    public List<Game> parse(List<Game> games) {
        driver = driverManager.getDriver();
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
            driver.navigate().to("http://ballchockdee.com" + game.getLink());
            game.setHalfTimeScore(findScore());
            List<OverUnder> overUnderList = findOverUnder();
            game.setOverUnderList(overUnderList);
            overUnderList.forEach(o -> o.setGame(game));
            logger.logGame(LogType.OKAY);
        } catch (TimeoutException e) {
            logger.logGame(LogType.ERROR);
        }
    }

    private String findScore() {
        Optional<WebElement> scoreElement = webDriverUtil.waitElement(By::className, "Score");
        if (scoreElement.isPresent()) {
            return scoreElement.get().getText();
        } else {
            return "-1:-1";
        }
    }

    private List<OverUnder> findOverUnder() {
        webDriverUtil.waitElement(By::className, "MarketT");
        Document document = Jsoup.parse(driver.getPageSource());
        Elements marketElements = document.select("div.MarketT");
        List<Element> overUnderMarketElements = marketElements.stream()
                .filter(market -> market.select("div.SubHead > span").text().contains("Over Under"))
                .collect(Collectors.toList());
        List<OverUnder> overUnderList = new ArrayList<>();
        for (Element marketElement : overUnderMarketElements) {
            Elements overUnderElements = marketElement.select("table > tbody > tr");
            for (Element overUnderElement : overUnderElements) {
                if (overUnderElement.className().equals("OddsClosed")) {
                    continue;
                }
                extractOverUnder(overUnderList, overUnderElement);
            }
        }
        return overUnderList;
    }

    private void extractOverUnder(List<OverUnder> overUnderList, Element overUnderElement) {
        double overSize = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabL > span.OddsM").text());
        double overCoefficient = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabL > span.OddsR").text());
        overUnderList.add(new OverUnder(OverUnder.Type.OVER.toString(), overSize, overCoefficient));
        double underSize = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsM").text());
        double underCoefficient = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsR").text());
        overUnderList.add(new OverUnder(OverUnder.Type.UNDER.toString(), underSize, underCoefficient));
    }
}
