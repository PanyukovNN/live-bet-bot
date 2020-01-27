package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.DriverManager;

import java.util.List;

public class ParseProcessor {

    private ParseProcessorLogger logger = new ParseProcessorLogger();

    private DriverManager driverManager;

    private GameParser gameParser;

    public ParseProcessor(DriverManager driverManager, GameParser gameParser) {
        this.driverManager = driverManager;
        this.gameParser = gameParser;
    }

    public List<Game> process() {
        try {
            driverManager.initiateDriver(true);
            logger.startLogMessage();
            List<Game> breakGames = gameParser.parse();
            logger.parsingComplete();
            return breakGames;
        } finally {
            driverManager.quitDriver();
        }
    }
}
