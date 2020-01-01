package com.zylex.livebetbot.service;

import com.zylex.livebetbot.DriverManager;
import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.model.Game;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ParseProcessor {

    private DriverManager driverManager;

    private GameDao gameDao;

    public ParseProcessor(DriverManager driverManager, GameDao gameDao) {
        this.driverManager = driverManager;
        this.gameDao = gameDao;
    }

    public List<Game> process() {
        try {
            WebDriver driver = driverManager.initiateDriver(false);
            List<Game> breakGames = new CountryParser(driver, gameDao.getNoResultGames()).parse();
            GameParser gameParser = new GameParser(driver);
            breakGames.forEach(gameParser::parse);
            return breakGames;
        } finally {
            driverManager.quitDriver();
        }
    }
}
