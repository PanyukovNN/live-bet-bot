package com.zylex.livebetbot.service;

import com.zylex.livebetbot.DriverManager;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.Goal;
import com.zylex.livebetbot.model.MoreLess;
import com.zylex.livebetbot.model.TotalMoreLess;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            List<Game> breakGames = findBreakGames(countryLinks);
            System.out.println();
            breakGames.forEach(System.out::println);
        } finally {
            driverManager.quitDriver();
        }
    }

    private void initDriver() {
        driver = driverManager.initiateDriver(false);
        wait = new WebDriverWait(driver, 5);
    }

    private List<Game> findBreakGames(List<String> countryLinks) {
        List<Game> games = new ArrayList<>();
        for (String countryLink : countryLinks) {
            driver.navigate().to("http://ballchockdee.com" + countryLink);
            waitElementWithId("bu:od:go:mt:2").click(); // click on gandicap
            waitElementsWithClassName("Hdp");
            Document document = Jsoup.parse(driver.getPageSource());
            Elements gameElements = document.select("table.Hdp > tbody > tr");
            for (Element gameElement : gameElements) {
//                Element dateTimeText = market.selectFirst("div.DateTimeTxt");
//                if (dateTimeText.text().contains("Перерыв")) {
                    // TODO check NPE
                    String firstTeam = gameElement.selectFirst("td > a.OddsTabL > span.OddsL").text();
                    String secondTeam = gameElement.selectFirst("td > a.OddsTabR > span.OddsL").text();
                    String gameLink = gameElement.selectFirst("td.Icons > a.IconMarkets").attr("href");
                    Game game = new Game(LocalDate.now(), firstTeam, secondTeam, gameLink);
                    if (!games.contains(game)) {
                        driver.navigate().to("http://ballchockdee.com" + game.getLink());
                        game.setBreakGoals(findGoal());
                        game.setTotalMoreLessList(findTotalMoreLess());
                        games.add(game);
                    }
//                }
            }
        }
        return games;
    }

    private Goal findGoal() {
        WebElement scoreElement = waitElementWithClassName("Score");
        String[] scores = scoreElement.getText().split(":");
        int homeGoals = Integer.parseInt(scores[0]);
        int awayGoals = Integer.parseInt(scores[1]);
        return new Goal(homeGoals, awayGoals);
    }

    private List<TotalMoreLess> findTotalMoreLess() {
        waitElementsWithClassName("MarketT");
        Document document = Jsoup.parse(driver.getPageSource());
        Elements marketElements = document.select("div.MarketT");
        List<Element> totalMoreLessMarketElements = marketElements.stream()
                .filter(market -> {
                    String header = market.select("div.SubHead > span").text();
                    return header.contains("тотал") || header.contains("Тотал");
                })
                .collect(Collectors.toList());
        List<TotalMoreLess> totalMoreLessList = new ArrayList<>();
        for (Element marketElement : totalMoreLessMarketElements) {
            Elements totalMoreLessElements = marketElement.select("table > tbody > tr");
            System.out.println(totalMoreLessElements.size());
            for (Element totalMoreLessElement : totalMoreLessElements) {
                double moreSize = Double.parseDouble(totalMoreLessElement.selectFirst("td > a.OddsTabL > span.OddsM").text());
                double moreCoefficient = Double.parseDouble(totalMoreLessElement.selectFirst("td > a.OddsTabL > span.OddsR").text());
                totalMoreLessList.add(new TotalMoreLess(MoreLess.MORE, moreSize, moreCoefficient));

                double lessSize = Double.parseDouble(totalMoreLessElement.selectFirst("td > a.OddsTabR > span.OddsM").text());
                double lessCoefficient = Double.parseDouble(totalMoreLessElement.selectFirst("td > a.OddsTabR > span.OddsR").text());
                totalMoreLessList.add(new TotalMoreLess(MoreLess.LESS, lessSize, lessCoefficient));
            }
        }
        return totalMoreLessList;
    }

    private WebElement waitElementWithId(String id) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return driver.findElement(By.id(id));
    }

    private WebElement waitElementWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElement(By.className(className));
    }

    private List<WebElement> waitElementsWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElements(By.className(className));
    }
}
