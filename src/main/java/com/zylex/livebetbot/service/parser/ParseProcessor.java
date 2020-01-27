package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.logger.ParseProcessorLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.openqa.selenium.WebDriver;

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
            WebDriver driver = driverManager.initiateDriver(true);
            logger.startLogMessage();
            List<Game> breakGames = new CountryParser(driver, gameRepository).parse();
            new GameParser(driver).parse(breakGames);
            logger.parsingComplete();
            return breakGames;
        } finally {
            driverManager.quitDriver();
        }
    }
}
