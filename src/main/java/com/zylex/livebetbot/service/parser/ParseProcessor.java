package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class ParseProcessor {

    private static final ParseProcessorLogger logger = new ParseProcessorLogger();

    private CountryFinder countryFinder;

    private CountryParser countryParser;

    private OverUnderParser overUnderParser;

    @Autowired
    public ParseProcessor(CountryFinder countryFinder, CountryParser countryParser, OverUnderParser overUnderParser) {
        this.countryFinder = countryFinder;
        this.countryParser = countryParser;
        this.overUnderParser = overUnderParser;
    }

    public Set<Game> process() {
        logger.startLogMessage();
        Set<Country> countries = countryFinder.findCountries();
        Set<Game> games = countryParser.parse(countries);
        //TODO think about low coupling
        Set<Game> appropriateGames = filterByRules(games);
        appropriateGames = overUnderParser.parse(appropriateGames);
        LogType logType = appropriateGames.isEmpty()
                ? LogType.NO_GAMES
                : LogType.OKAY;
        logger.parsingComplete(logType);
        return appropriateGames;
    }

    private Set<Game> filterByRules(Set<Game> extractedGames) {
        Set<Game> appropriateGames = new LinkedHashSet<>();
        for (Game game : extractedGames) {
            for (RuleNumber ruleNumber : RuleNumber.values()) {
                if (ruleNumber.gameTime.checkTime(game.getGameTime())) {
                    if (ruleNumber.score.equals(game.getScanTimeScore())) {
                        appropriateGames.add(game);
//                        if (!appropriateGames.contains(game)) {
//                            appropriateGames.add(game);
//                        }
                    }
                }
            }
        }
        return appropriateGames;
    }
}
