package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.CountryParserLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.CountryParserException;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.League;
import com.zylex.livebetbot.service.driver.DriverManager;
import com.zylex.livebetbot.service.repository.CountryRepository;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.repository.LeagueRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

@Service
public class CountryParser {

    private static final CountryParserLogger logger = new CountryParserLogger();

    private DriverManager driverManager;

    private GameRepository gameRepository;

    private CountryRepository countryRepository;

    private LeagueRepository leagueRepository;

    @Autowired
    public CountryParser(DriverManager driverManager, GameRepository gameRepository,
                         CountryRepository countryRepository, LeagueRepository leagueRepository) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
        this.countryRepository = countryRepository;
        this.leagueRepository = leagueRepository;
    }

    public List<Game> parse() {
        try {
            Set<Country> countries = parseCountryLinks();
            if (countries.isEmpty()) {
                logger.logCountriesFound(LogType.NO_COUNTRIES);
                return Collections.emptyList();
            }
            logger.logCountriesFound(LogType.OKAY);
            logger.startLogMessage(countries.size());
            return findBreakGames(countries);
        } catch (IOException e) {
            throw new CountryParserException(e.getMessage(), e);
        }
    }

    private Set<Country> parseCountryLinks() throws IOException {
        Set<Country> countries = extractCountryLinks();
        return countryRepository.save(countries);
    }

    private Set<Country> extractCountryLinks() throws IOException {
        Set<Country> countries = new LinkedHashSet<>();
        Document document = Jsoup.connect("http://www.ballchockdee.com/euro/live-betting/football")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170")
                .referrer("http://www.google.com")
                .get();
        Elements elements = document.select("ul#ms-live-res-ul-1 > li.Unsel > a");
        for (Element element : elements) {
            String countryName = element.select("div").first().text().replace("\\d+", "");
            String countryLink = element.attr("href");
            Country country = new Country(countryName, countryLink);
            countries.add(country);
        }
        return countries;
    }

    private List<Game> findBreakGames(Set<Country> countries) {
        List<Game> games = new ArrayList<>();
        List<Game> noResultGames = gameRepository.getWithoutResult();
        for (Country country : countries) {
            if (!openHandicapTab(country.getLink())) {
                continue;
            }
            Document document = Jsoup.parse(driverManager.getDriver().getPageSource());
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
//        country.getLeagues().add(league);
        return leagueRepository.save(league);
    }

    private void establishDependencies(Country country, League league, List<Game> extractedGames) {
        extractedGames.forEach(game -> {
            game.setCountry(country);
            game.setLeague(league);
        });
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
        try {
            driverManager.getDriver().navigate().to("http://ballchockdee.com" + countryLink);
            waitElement(By::id, "bu:od:go:mt:2").click();
            waitElement(By::className, "Hdp");
            logger.logCountry(LogType.OKAY);
            return true;
        } catch (WebDriverException e) {
            logger.logCountry(LogType.ERROR);
            return false;
        }
    }

    private WebElement waitElement(Function<String, By> byFunction, String elementName) {
        driverManager.getWait().ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(byFunction.apply(elementName)));
        return driverManager.getDriver().findElement(byFunction.apply(elementName));
    }
}
