package com.zylex.livebetbot.service;

import com.zylex.livebetbot.DriverManager;
import com.zylex.livebetbot.model.Game;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class ParseProcessor {

    private DriverManager driverManager;

    public ParseProcessor(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    public List<Game> process() {
        try {
            WebDriver driver = driverManager.initiateDriver(false);
            List<Game> breakGames = new CountryParser(driver).parse();
            GameParser gameParser = new GameParser(driver);
            breakGames.forEach(gameParser::parse);
            return breakGames;
        } finally {
            driverManager.quitDriver();
        }
    }
}
