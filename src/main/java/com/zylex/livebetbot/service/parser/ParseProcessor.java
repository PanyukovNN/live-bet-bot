package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParserLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.DriverManager;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        AtomicInteger i = new AtomicInteger();
        return breakGames;
    }
}
