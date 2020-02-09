package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.CountryParserLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.League;
import com.zylex.livebetbot.service.driver.DriverManager;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.repository.LeagueRepository;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Service
public class CountryParser {

    private static final CountryParserLogger logger = new CountryParserLogger();

    private DriverManager driverManager;

    private GameRepository gameRepository;

    private LeagueRepository leagueRepository;

    @Autowired
    public CountryParser(DriverManager driverManager, GameRepository gameRepository, LeagueRepository leagueRepository) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
        this.leagueRepository = leagueRepository;
    }

    public List<Game> parse(Set<Country> countries) {
        if (countries.isEmpty()) {
            return Collections.emptyList();
        }
        logger.startLogMessage(countries.size());
        return parseCountries(countries);
    }

    private List<Game> parseCountries(Set<Country> countries) {
        //TODO remake to LinkedHashSet
        List<Game> games = new ArrayList<>();
        List<Game> noResultGames = gameRepository.getWithoutResult();
        for (Country country : countries) {
            if (!openHandicapTab(country.getLink())) {
                continue;
            }
            parseLeagues(games, noResultGames, country);
        }
        return games;
    }

    private void parseLeagues(List<Game> games, List<Game> noResultGames, Country country) {
        Document document = Jsoup.parse(driverManager.getDriver().getPageSource());
        Elements leagueTitleElements = document.select("div.MarketLea");
        Elements leagueGamesElements = document.select("table.Hdp");
        for (int i = 0; i < leagueGamesElements.size(); i++) {
            League league = extractLeague(country, leagueTitleElements.get(i));
            Elements gameElements = leagueGamesElements.get(i).select("tbody > tr");
            List<Game> extractedGames = extractGames(games, gameElements, noResultGames);
            List<Game> appropriateGames = filterByRules(extractedGames);
            establishDependencies(country, league, appropriateGames);
            for (Game game : appropriateGames) {
                if (!games.contains(game)) {
                    games.add(game);
                }
            }
        }
    }

    private List<Game> filterByRules(List<Game> extractedGames) {
        List<Game> appropriateGames = new ArrayList<>();
        for (Game game : extractedGames) {
            for (RuleNumber ruleNumber : RuleNumber.values()) {
                if (ruleNumber.gameTime.checkTime(game.getGameTime())) {
                    if (ruleNumber.score.equals(game.getScanTimeScore())) {
                        appropriateGames.add(game);
                    }
                }
            }
        }
        return appropriateGames;
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
    }

    private List<Game> extractGames(List<Game> games, Elements gameElements, List<Game> noResultGames) {
        List<Game> extractedGames = new ArrayList<>();
        for (Element gameElement : gameElements) {
            String[] scoreTimeTest = gameElement.selectFirst("div.DateTimeTxt").text().split(" ", 2);
            String scanTimeScore = scoreTimeTest[0].replace("-", ":");
            String gameTime = scoreTimeTest[1];
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
            Game game = new Game(LocalDateTime.now(), gameTime, firstTeamElement.text(), secondTeamElement.text(), scanTimeScore, gameLink);
            extractedGames.add(game);
        }
        return extractedGames;
    }

    private boolean openHandicapTab(String countryLink) {
        try {
            driverManager.getDriver().navigate().to("http://ballchockdee.com" + countryLink);
            driverManager.waitElement(By::id, "bu:od:go:mt:2").click();
            driverManager.waitElement(By::className, "Hdp");
            logger.logCountry(LogType.OKAY);
            return true;
        } catch (WebDriverException e) {
            logger.logCountry(LogType.ERROR);
            return false;
        }
    }
}
