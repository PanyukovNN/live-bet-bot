package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParserLogger;
import com.zylex.livebetbot.exception.CountryParserException;
import com.zylex.livebetbot.model.Game;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@SuppressWarnings("WeakerAccess")
public class CountryParser {

    private WebDriver driver;

    private WebDriverWait wait;

    private List<Game> noResultGames;

    private ParserLogger logger;

    CountryParser(WebDriver driver, List<Game> noResultGames, ParserLogger logger) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 60);
        this.noResultGames = noResultGames;
        this.logger = logger;
    }

    List<Game> parse() {
        try {
            List<String> countryLinks = parseCountryLinks();
            if (countryLinks.isEmpty()) {
                logger.logCountriesFound(LogType.NO_COUNTRIES);
                return new ArrayList<>();
            }
            logger.logCountriesFound(LogType.OKAY);
            logger.startLogMessage(LogType.COUNTRIES, countryLinks.size());
            List<Game> breakGames = findBreakGames(countryLinks);
            if (breakGames.isEmpty()) {
                logger.startLogMessage(LogType.NO_GAMES, 0);
            } else {
                logger.startLogMessage(LogType.GAMES, breakGames.size());
            }
            return breakGames;
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
        try {
            for (String countryLink : countryLinks) {
                if (prepareWebpage(countryLink)) {
                    logger.logCountry(LogType.OKAY);
                } else {
                    logger.logCountry(LogType.ERROR);
                    continue;
                }
                Document document = Jsoup.parse(driver.getPageSource());
                Elements gameElements = document.select("table.Hdp > tbody > tr");
                for (Element gameElement : gameElements) {
                    Element dateTimeText = gameElement.selectFirst("div.DateTimeTxt");
                    if (dateTimeText.text().contains("Перерыв")) {
                        Element firstTeamElement = gameElement.selectFirst("td > a.OddsTabL > span.OddsL");
                        Element secondTeamElement = gameElement.selectFirst("td > a.OddsTabR > span.OddsL");
                        if (firstTeamElement == null || secondTeamElement == null) {
                            continue;
                        }
                        String gameLink = gameElement.selectFirst("td.Icons > a.IconMarkets").attr("href");
                        Game game = new Game(0, LocalDateTime.now(), firstTeamElement.text(), secondTeamElement.text(), gameLink);
                        if (noResultGames.contains(game) | games.contains(game)) {
                            continue;
                        }
                        games.add(game);
                    }
                }
            }
        } catch (Exception e) {
            //TODO may be removed
            System.out.print("\nПроизошла ошибка при сканировании стран: " + e.getMessage());
            e.printStackTrace();
        }
        return games;
    }

    private boolean prepareWebpage(String countryLink) {
        try {
            driver.navigate().to("http://ballchockdee.com" + countryLink);
            wait.ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("bu:od:go:mt:2")));
            driver.findElement(By.id("bu:od:go:mt:2")).click();
            wait.ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.presenceOfElementLocated(By.className("Hdp")));
            return true;
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }
}
