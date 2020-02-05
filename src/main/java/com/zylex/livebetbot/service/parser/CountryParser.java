package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.CountryParserLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.League;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.repository.CountryRepository;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.repository.LeagueRepository;
import com.zylex.livebetbot.service.util.AttemptsUtil;
import com.zylex.livebetbot.service.util.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings("WeakerAccess")
@Service
public class CountryParser {

    private DriverManager driverManager;

    private WebDriver driver;

    private GameRepository gameRepository;

    private CountryRepository countryRepository;

    private LeagueRepository leagueRepository;

    private WebDriverUtil webDriverUtil;

    private CountryParserLogger logger = new CountryParserLogger();

    @Autowired
    public CountryParser(DriverManager driverManager, GameRepository gameRepository,
                         CountryRepository countryRepository, LeagueRepository leagueRepository,
                         WebDriverUtil webDriverUtil) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
        this.countryRepository = countryRepository;
        this.leagueRepository = leagueRepository;
        this.webDriverUtil = webDriverUtil;
    }

    public List<Game> parse() {
        driver = driverManager.getDriver();
        Set<Country> countries = parseCountryLinks();
        if (countries.isEmpty()) {
            logger.logCountriesFound(LogType.NO_COUNTRIES);
            return new ArrayList<>();
        }
        logger.logCountriesFound(LogType.OKAY);
        logger.startLogMessage(countries.size());
        return findBreakGames(countries);
    }

    private Set<Country> parseCountryLinks() {
        Set<Country> countries = new LinkedHashSet<>();
        AttemptsUtil.attempt(this::extractCountryLinks, countries, 5);
        countries = countryRepository.save(countries);
        return countries;
    }

    private void extractCountryLinks(Set<Country> countries) {
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
        } catch (IOException e) {
            //TODO log attempt error
        }
    }

    private List<Game> findBreakGames(Set<Country> countries) {
        List<Game> games = new ArrayList<>();
        List<Game> noResultGames = gameRepository.getWithoutResult();
        for (Country country : countries) {
            if (!openHandicapTab(country.getLink())) {
                continue;
            }
            Document document = Jsoup.parse(driver.getPageSource());
            Elements leagueTitleElements = document.select("div.MarketLea");
            Elements leagueGamesElements = document.select("table.Hdp");
            for (int i = 0; i < leagueGamesElements.size(); i++) {
                League league = extractLeague(country, leagueTitleElements.get(i));
                Elements gameElements = leagueGamesElements.get(i).select("tbody > tr");
                List<Game> extractedGames = extractGames(games, gameElements, noResultGames);
                establishDependencies(country, league, extractedGames);
                for (Game game : extractedGames) {
                    if (!games.contains(game)) {
                        games.add(game);
                    }
                }
            }
        }
        return games;
    }

    private League extractLeague(Country country, Element leagueTitleElement) {
        String leagueName = leagueTitleElement.select("div.MarketLea > div.SubHeadT").first().text();
        League league = new League(leagueName);
        league.setCountry(country);
        return leagueRepository.save(league);
    }

    private void establishDependencies(Country country, League league, List<Game> extractedGames) {
        extractedGames.forEach(game -> {
            game.setCountry(country);
            game.setLeague(league);
        });
        country.getLeagues().add(league);
    }

    private List<Game> extractGames(List<Game> games, Elements gameElements, List<Game> noResultGames) {
        List<Game> extractedGames = new ArrayList<>();
        for (Element gameElement : gameElements) {
            Element dateTimeText = gameElement.selectFirst("div.DateTimeTxt");
            if (dateTimeText.text().contains("HT")) {
                Element firstTeamElement = gameElement.selectFirst("td > a.OddsTabL > span.OddsL");
                Element secondTeamElement = gameElement.selectFirst("td > a.OddsTabR > span.OddsL");
                if (firstTeamElement == null || secondTeamElement == null) {
                    continue;
                }
                String gameLink = gameElement.selectFirst("td.Icons > a.IconMarkets").attr("href");
                boolean noResultContains = noResultGames.stream().anyMatch(g -> g.getLink().equals(gameLink));
                boolean gamesContains = games.stream().anyMatch(g -> g.getLink().equals(gameLink));
                if (noResultContains || gamesContains) {
                    continue;
                }
                Game game = new Game(LocalDateTime.now(), firstTeamElement.text(), secondTeamElement.text(), gameLink);
                extractedGames.add(game);
            }
        }
        return extractedGames;
    }

    private boolean openHandicapTab(String countryLink) {
        driver.navigate().to("http://ballchockdee.com" + countryLink);
        Optional<WebElement> handicapTab = webDriverUtil.waitElement(By::id, "bu:od:go:mt:2");
        if (handicapTab.isPresent()) {
            handicapTab.get().click();
            webDriverUtil.waitElement(By::className, "Hdp");
            logger.logCountry(LogType.OKAY);
            return true;
        } else {
            logger.logCountry(LogType.ERROR);
            return false;
        }
    }
}
