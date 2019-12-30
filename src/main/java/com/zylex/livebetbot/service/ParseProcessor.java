package com.zylex.livebetbot.service;

import com.zylex.livebetbot.DriverManager;
import com.zylex.livebetbot.model.Game;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ParseProcessor {

    private static WebDriverWait wait;

    private static WebDriver driver;

    private CountryParser countryParser;

    private DriverManager driverManager;

    public ParseProcessor(DriverManager driverManager, CountryParser countryParser) {
        this.driverManager = driverManager;
        this.countryParser = countryParser;
    }

    public void process() {
        List<String> countryLinks = countryParser.parse();
        try {
            initDriver();
            List<Game> games = parseGames(countryLinks);
            games.forEach(System.out::println);
        } finally {
            driverManager.quitDriver();
        }
    }

    private void initDriver() {
        driver = driverManager.initiateDriver(false);
        wait = new WebDriverWait(driver, 5);
    }

    private List<Game> parseGames(List<String> countryLinks) {
        List<Game> games = new ArrayList<>();
        for (String countryLink : countryLinks) {
            driver.navigate().to("http://ballchockdee.com" + countryLink);
            waitElementWithId("bu:od:go:mt:2").click(); // click on gandicap
            waitElementsWithClassName("Hdp");
            Document document = Jsoup.parse(driver.getPageSource());
            Elements markets = document.select("table.Hdp > tbody > tr");
            System.out.println(markets.size());
            for (Element market : markets) {
//                Element dateTimeText = market.selectFirst("div.DateTimeTxt");
//                if (dateTimeText.text().contains("Перерыв")) {
//                    System.out.println("fuck yeah");
//                }
                String firstTeam = market.selectFirst("td > a.OddsTabL > span.OddsL").text();
                String secondTeam = market.selectFirst("td > a.OddsTabR > span.OddsL").text();
                String gameLink = market.selectFirst("td.Icons > a.IconMarkets").attr("href");
                games.add(new Game(LocalDate.now(), firstTeam, secondTeam, gameLink));
            }
        }
        return games;
    }

    private WebElement waitElementWithId(String id) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return driver.findElement(By.id(id));
    }

    private List<WebElement> waitElementsWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElements(By.className(className));
    }
}
