package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.GameParserLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.OverUnder;
import com.zylex.livebetbot.service.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Service
public class GameParser {

    private WebDriver driver;

    private DriverManager driverManager;

    private WebDriverWait wait;

    private CountryParser countryParser;

    private GameParserLogger logger = new GameParserLogger();

    @Autowired
    public GameParser(DriverManager driverManager, CountryParser countryParser) {
        this.driverManager = driverManager;
        this.countryParser = countryParser;
    }

    public List<Game> parse() {
        List<Game> games = countryParser.parse();
        initDriver();
        if (games.isEmpty()) {
            logger.startLogMessage(LogType.NO_GAMES, 0);
            return games;
        }
        logger.startLogMessage(LogType.OKAY, games.size());
        games.forEach(this::parseSingleGame);
        return games;
    }

    private void initDriver() {
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, 60);
    }

    private void parseSingleGame(Game game) {
        try {
            driver.navigate().to("http://ballchockdee.com" + game.getLink());
            game.setHalfTimeScore(findScore());
            Set<OverUnder> overUnderList = findOverUnder();
            game.setOverUnderSet(overUnderList);
            overUnderList.forEach(o -> o.setGame(game));
            logger.logGame(LogType.OKAY);
        } catch (TimeoutException e) {
            logger.logGame(LogType.ERROR);
        }
    }

    private String findScore() {
        try {
            WebElement scoreElement = waitElementWithClassName("Score");
            return scoreElement.getText();
        } catch (StaleElementReferenceException | TimeoutException e) {
            return "-1:-1";
        }
    }

    private Set<OverUnder> findOverUnder() {
        waitElementWithClassName("MarketT");
        Document document = Jsoup.parse(driver.getPageSource());
        Elements marketElements = document.select("div.MarketT");
        List<Element> overUnderMarketElements = marketElements.stream()
                .filter(market -> market.select("div.SubHead > span").text().contains("Over Under"))
                .collect(Collectors.toList());
        Set<OverUnder> overUnderSet = new HashSet<>();
        for (Element marketElement : overUnderMarketElements) {
            Elements overUnderElements = marketElement.select("table > tbody > tr");
            for (Element overUnderElement : overUnderElements) {
                if (overUnderElement.className().equals("OddsClosed")) {
                    continue;
                }
                extractOverUnder(overUnderSet, overUnderElement);
            }
        }
        return overUnderSet;
    }

    private void extractOverUnder(Set<OverUnder> overUnderSet, Element overUnderElement) {
        double overSize = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabL > span.OddsM").text());
        double overCoefficient = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabL > span.OddsR").text());
        overUnderSet.add(new OverUnder(OverUnder.Type.OVER.toString(), overSize, overCoefficient));
        double underSize = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsM").text());
        double underCoefficient = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsR").text());
        overUnderSet.add(new OverUnder(OverUnder.Type.UNDER.toString(), underSize, underCoefficient));
    }

    private WebElement waitElementWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElement(By.className(className));
    }
}
