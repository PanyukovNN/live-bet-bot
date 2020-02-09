package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Country;
import com.zylex.livebetbot.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Game> breakGames = overUnderParser.parse(games);
        if (breakGames.isEmpty()) {
            logger.parsingComplete(LogType.NO_GAMES);
        } else {
            logger.parsingComplete(LogType.OKAY);
        }
        return breakGames;
    }
}
