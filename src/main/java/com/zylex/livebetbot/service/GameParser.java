package com.zylex.livebetbot.service;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.Goal;
import com.zylex.livebetbot.model.MoreLess;
import com.zylex.livebetbot.model.Tml;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class GameParser {

    private WebDriver driver;

    private WebDriverWait wait;

    GameParser(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 15);
    }

    void parse(Game game) {
        driver.navigate().to("http://ballchockdee.com" + game.getLink());
        game.setBreakGoals(findGoal());
        game.setTmlList(findTml());
    }

    private Goal findGoal() {
        WebElement scoreElement = waitElementWithClassName("Score");
        String[] scores = scoreElement.getText().split(":");
        int homeGoals = Integer.parseInt(scores[0]);
        int awayGoals = Integer.parseInt(scores[1]);
        return new Goal(homeGoals, awayGoals);
    }

    private List<Tml> findTml() {
        waitElementWithClassName("MarketT");
        Document document = Jsoup.parse(driver.getPageSource());
        Elements marketElements = document.select("div.MarketT");
        List<Element> tmlMarketElements = marketElements.stream()
                .filter(market -> {
                    String header = market.select("div.SubHead > span").text();
                    return header.contains("Первая половина: тотал (больше/меньше)") || header.contains("Тотал (больше/меньше)");
                })
                .collect(Collectors.toList());
        List<Tml> TmlList = new ArrayList<>();
        for (Element marketElement : tmlMarketElements) {
            Elements tmlElements = marketElement.select("table > tbody > tr");
            for (Element tmlElement : tmlElements) {
                if (tmlElement.className().equals("OddsClosed")) {
                    continue;
                }
                try {
                    double moreSize = Double.parseDouble(tmlElement.selectFirst("td > a.OddsTabL > span.OddsM").text());
                    double moreCoefficient = Double.parseDouble(tmlElement.selectFirst("td > a.OddsTabL > span.OddsR").text());
                    TmlList.add(new Tml(0, 0, MoreLess.MORE, moreSize, moreCoefficient));

                    double lessSize = Double.parseDouble(tmlElement.selectFirst("td > a.OddsTabR > span.OddsM").text());
                    double lessCoefficient = Double.parseDouble(tmlElement.selectFirst("td > a.OddsTabR > span.OddsR").text());
                    TmlList.add(new Tml(0, 0, MoreLess.LESS, lessSize, lessCoefficient));
                } catch (NullPointerException e) {
                    System.out.println("Exception when parsing this: " + tmlElement + "; on page: " + driver.getCurrentUrl());
                }
            }
        }
        return TmlList;
    }

    private WebElement waitElementWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElement(By.className(className));
    }
}
