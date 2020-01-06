package com.zylex.livebetbot.service.parser;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ParserLogger;
import com.zylex.livebetbot.model.Game;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ParseProcessor {

    private ParserLogger logger = new ParserLogger();

    private WebDriver driver;

    private GameDao gameDao;

    public ParseProcessor(WebDriver driver, GameDao gameDao) {
        this.driver = driver;
        this.gameDao = gameDao;
    }

    public List<Game> process() {
        logger.startLogMessage(LogType.PARSING_START, 0);
        List<Game> breakGames = new CountryParser(driver, gameDao.getNoResultGames(), logger).parse();
        GameParser gameParser = new GameParser(driver, logger);
        breakGames.forEach(gameParser::parse);
        return breakGames;
    }
}
