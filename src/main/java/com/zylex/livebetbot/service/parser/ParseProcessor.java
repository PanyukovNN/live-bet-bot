package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public List<Game> process() {
        logger.startLogMessage();
        Set<Country> countries = countryFinder.findCountries();
        List<Game> games = countryParser.parse(countries);
        //TODO think about low coupling
        List<Game> appropriateGames = filterByRules(games);
        appropriateGames = overUnderParser.parse(appropriateGames);
        LogType logType = appropriateGames.isEmpty()
                ? LogType.NO_GAMES
                : LogType.OKAY;
        logger.parsingComplete(logType);
        return appropriateGames;
    }

    private List<Game> filterByRules(List<Game> extractedGames) {
        List<Game> appropriateGames = new ArrayList<>();
        extractedGames.forEach(game -> Arrays.stream(RuleNumber.values())
                .filter(ruleNumber -> ruleNumber.gameTime.checkTime(game.getGameTime()))
                .filter(ruleNumber -> ruleNumber.score.equals(game.getScanTimeScore()))
                .map(ruleNumber -> game)
                .forEach(appropriateGames::add));
        return appropriateGames;
    }
}
