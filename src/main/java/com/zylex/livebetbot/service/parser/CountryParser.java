package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.CountryParserLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.CountryParserException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.repository.GameRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@SuppressWarnings("WeakerAccess")
@Service
public class CountryParser {

    private DriverManager driverManager;

    private WebDriver driver;

    private WebDriverWait wait;

    private GameRepository gameRepository;

    private CountryParserLogger logger = new CountryParserLogger();

    @Autowired
    public CountryParser(DriverManager driverManager, GameRepository gameRepository) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
    }

    public List<Game> parse() {
        try {
            initDriver();
            List<Country> countries = parseCountryLinks();
            if (countries.isEmpty()) {
                logger.logCountriesFound(LogType.NO_COUNTRIES);
                return new ArrayList<>();
            }
            logger.logCountriesFound(LogType.OKAY);
            logger.startLogMessage(countries.size());
            return findBreakGames(countries);
        } catch (IOException e) {
            throw new CountryParserException(e.getMessage(), e);
        }
    }

    private void initDriver() {
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, 10);
    }

    private List<Country> parseCountryLinks() throws IOException {
        List<Country> countries = new ArrayList<>();
        int attempts = 5;
        while (attempts-- > 0) {
            try {
                Document document = Jsoup.connect("http://www.ballchockdee.com/euro/live-betting/football")
                        .userAgent("Chrome/4.0.249.0 Safari/532.5")
                        .referrer("http://www.google.com")
                        .get();
                Elements elements = document.select("ul#ms-live-res-ul-1 > li.Unsel > a");
                for (Element element : elements) {
                    String countryName = element.select("div").first().text().replaceFirst("\\d+", "");
                    String countryLink = element.attr("href");
                    Country country = new Country(countryName, countryLink);
                    countries.add(country);
                }
                break;
            } catch (UnknownHostException | ConnectException ignore) {
            }
        }
        //TODO log
        return countries;
    }

    private List<Game> findBreakGames(List<Country> countries) {
        List<Game> games = new ArrayList<>();
        List<Game> noResultGames = gameRepository.getWithoutResult();
        for (Country country : countries) {
            if (!prepareWebpage(country)) {
                continue;
            }
            Document document = Jsoup.parse(driver.getPageSource());
            Elements leagueElements = document.select("div.MarketBd");
            for (Element leagueElement : leagueElements) {
                String league = leagueElement.select("div.MarketLea > div.SubHeadT").first().text();
                Elements gameElements = leagueElement.select("table.Hdp > tbody > tr");
                extractGame(games, country.getName(), league, gameElements, noResultGames);
            }
        }
        return games;
    }

    private void extractGame(List<Game> games, String countryName, String league, Elements gameElements, List<Game> noResultGames) {
        for (Element gameElement : gameElements) {
            Element dateTimeText = gameElement.selectFirst("div.DateTimeTxt");
//            if (dateTimeText.text().contains("HT")) {
                Element firstTeamElement = gameElement.selectFirst("td > a.OddsTabL > span.OddsL");
                Element secondTeamElement = gameElement.selectFirst("td > a.OddsTabR > span.OddsL");
                if (firstTeamElement == null || secondTeamElement == null) {
                    continue;
                }
                String gameLink = gameElement.selectFirst("td.Icons > a.IconMarkets").attr("href");
                Game game = new Game(LocalDateTime.now(), countryName, league, firstTeamElement.text(), secondTeamElement.text(), gameLink);
                boolean noResultContains = noResultGames.stream().anyMatch(g -> g.getLink().equals(game.getLink()));
                if (noResultContains || games.contains(game)) {
                    continue;
                }
                games.add(game);
//            }
        }
    }

    private boolean prepareWebpage(Country country) {
        int attempts = 2;
        while (attempts-- > 0) {
            try {
                openHandicapTab(country.getLink());
                logger.logCountry(LogType.OKAY);
                return true;
            } catch (NoSuchElementException | TimeoutException ignore) {
            }
        }
        logger.logCountry(LogType.ERROR);
        return false;
    }

    private void openHandicapTab(String countryLink) {
        driver.navigate().to("http://ballchockdee.com" + countryLink);
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.id("bu:od:go:mt:2")));
        driver.findElement(By.id("bu:od:go:mt:2")).click();
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className("Hdp")));
    }

    private static class Country {

        private String name;

        private String link;

        public Country(String name, String link) {
            this.name = name;
            this.link = link;
        }

        public String getName() {
            return name;
        }

        public String getLink() {
            return link;
        }
    }
}
