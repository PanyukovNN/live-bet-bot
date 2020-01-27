package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.repository.GameRepository;

import java.util.List;

public class ParseProcessor {

    private ParseProcessorLogger logger = new ParseProcessorLogger();

    private DriverManager driverManager;

    private final GameRepository gameRepository;

    public ParseProcessor(DriverManager driverManager, GameRepository gameRepository) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
    }

    public List<Game> process() {
        try {
            driverManager.initiateDriver(true);
            logger.startLogMessage();
            List<Game> breakGames = new GameParser(
                new CountryParser(
                    driverManager,
                    gameRepository)
            ).parse();
            logger.parsingComplete();
            return breakGames;
        } finally {
            driverManager.quitDriver();
        }
    }
}
