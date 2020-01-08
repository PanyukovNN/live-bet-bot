package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.ParserLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.Goal;
import com.zylex.livebetbot.model.OverUnder;
import com.zylex.livebetbot.model.OverUnderType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class GameParser {

    private WebDriver driver;

    private WebDriverWait wait;

    private ParserLogger logger;

    GameParser(WebDriver driver, ParserLogger logger) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 60);
        this.logger = logger;
    }

    void parse(Game game) {
        logger.logGame();
        driver.navigate().to("http://ballchockdee.com" + game.getLink());
        game.setBreakGoal(findGoal());
        game.setOverUnderList(findOverUnder());
    }

    private Goal findGoal() {
        try {
            WebElement scoreElement = waitElementWithClassName("Score");
            String[] scores = scoreElement.getText().split(":");
            int homeGoals = Integer.parseInt(scores[0]);
            int awayGoals = Integer.parseInt(scores[1]);
            return new Goal(homeGoals, awayGoals);
        } catch (StaleElementReferenceException | TimeoutException e) {
            return new Goal(-1, -1);
        } catch (Exception e) {
            e.printStackTrace();
            return new Goal(-1, -1);
        }
    }

    private List<OverUnder> findOverUnder() {
        waitElementWithClassName("MarketT");
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
        overUnderList.add(new OverUnder(0, 0, OverUnderType.OVER, overSize, overCoefficient));
        double underSize = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsM").text());
        double underCoefficient = Double.parseDouble(overUnderElement.selectFirst("td > a.OddsTabR > span.OddsR").text());
        overUnderList.add(new OverUnder(0, 0, OverUnderType.UNDER, underSize, underCoefficient));
    }

    private WebElement waitElementWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElement(By.className(className));
    }
}
