package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.LeagueToScan;
import com.zylex.livebetbot.service.repository.LeagueRepository;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ParseProcessor {

    private static final ParseProcessorLogger logger = new ParseProcessorLogger();

    private CountryFinder countryFinder;

    private CountryParser countryParser;

    private OverUnderParser overUnderParser;

    private LeagueRepository leagueRepository;

    @Autowired
    public ParseProcessor(CountryFinder countryFinder, CountryParser countryParser, OverUnderParser overUnderParser, LeagueRepository leagueRepository) {
        this.countryFinder = countryFinder;
        this.countryParser = countryParser;
        this.overUnderParser = overUnderParser;
        this.leagueRepository = leagueRepository;
    }

    public Set<Game> process() {
        logger.startLogMessage();
        Set<Country> countries = countryFinder.findCountries();
        Set<Game> games = countryParser.parse(countries);
        games = filterByRules(games);
        games = filterByLeagues(games);
        games = overUnderParser.parse(games);
        LogType logType = games.isEmpty()
                ? LogType.NO_GAMES
                : LogType.OKAY;
        logger.parsingComplete(logType);
        return games;
    }

    private Set<Game> filterByRules(Set<Game> extractedGames) {
        Set<Game> appropriateGames = new LinkedHashSet<>();
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

    private Set<Game> filterByLeagues(Set<Game> games) {
        List<LeagueToScan> leaguesToScan = leagueRepository.getLeaguesToScan();
        Set<Game> appropriateGames = new LinkedHashSet<>();
        for (Game game : games) {
            if (leaguesToScan.stream().anyMatch(lts -> lts.getName().equals(game.getLeague().getName()))) {
                appropriateGames.add(game);
            }
        }
        return appropriateGames;
    }
}
