package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParseProcessor {

    private static final ParseProcessorLogger logger = new ParseProcessorLogger();

    private CountryParser countryParser;

    private GameParser gameParser;

    @Autowired
    public ParseProcessor(CountryParser countryParser, GameParser gameParser) {
        this.countryParser = countryParser;
        this.gameParser = gameParser;
    }

    public List<Game> process() {
        logger.startLogMessage();
        List<Game> games = countryParser.parse();
        List<Game> breakGames = gameParser.parse(games);
        if (breakGames.isEmpty()) {
            logger.parsingComplete(LogType.NO_GAMES);
        } else {
            logger.parsingComplete(LogType.OKAY);
        }
        return breakGames;
    }
}
