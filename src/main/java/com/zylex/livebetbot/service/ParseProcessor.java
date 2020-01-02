package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParserLogger;
import com.zylex.livebetbot.model.Game;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ParseProcessor {

    private ParserLogger logger = new ParserLogger();

    private DriverManager driverManager;

    private GameDao gameDao;

    public ParseProcessor(DriverManager driverManager, GameDao gameDao) {
        this.driverManager = driverManager;
        this.gameDao = gameDao;
    }

    public List<Game> process() {
        logger.startLogMessage(LogType.PARSING_START, 0);
        WebDriver driver = driverManager.getDriver();
        List<Game> breakGames = new CountryParser(driver, gameDao.getNoResultGames(), logger).parse();
        GameParser gameParser = new GameParser(driver, logger);
        breakGames.forEach(gameParser::parse);
        return breakGames;
    }
}
