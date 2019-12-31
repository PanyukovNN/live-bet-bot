package com.zylex.livebetbot.service;

import com.zylex.livebetbot.exception.CountryParserException;
import com.zylex.livebetbot.model.Game;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CountryParser {

    private WebDriver driver;

    private WebDriverWait wait;

    CountryParser(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 5);
    }

    List<Game> parse() {
        try {
            return findBreakGames(
                    parseCountryLinks()
            );
        } catch (IOException e) {
            throw new CountryParserException(e.getMessage(), e);
        }
    }

    private List<String> parseCountryLinks() throws IOException {
        Document document = Jsoup.connect("http://ballchockdee.com/ru-ru/euro/ставки-live/футбол")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elements = document.select("ul#ms-live-res-ul-1 > li.Unsel > a");
        List<String> countryLinks = new ArrayList<>();
        elements.forEach(element -> countryLinks.add(element.attr("href")));
        return countryLinks;
    }

    private List<Game> findBreakGames(List<String> countryLinks) {
        List<Game> games = new ArrayList<>();
        for (String countryLink : countryLinks) {
            prepareWebpage(countryLink);
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
                    games.add(game);
                }
//                }
            }
        }
        return games;
    }

    private void prepareWebpage(String countryLink) {
        driver.navigate().to("http://ballchockdee.com" + countryLink);
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("bu:od:go:mt:2")));
        driver.findElement(By.id("bu:od:go:mt:2")).click();
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className("Hdp")));
    }
}
