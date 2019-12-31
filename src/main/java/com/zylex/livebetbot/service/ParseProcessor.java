package com.zylex.livebetbot.service;

import com.zylex.livebetbot.DriverManager;
import com.zylex.livebetbot.model.Game;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ParseProcessor {

    private static WebDriverWait wait;

    private static WebDriver driver;

    private DriverManager driverManager;

    public ParseProcessor(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    public void process() {
        try {
            initDriver();
            List<Game> breakGames = new CountryParser(driver).parse();
            System.out.println();
            breakGames.forEach(System.out::println);
            GameParser gameParser = new GameParser(driver);
            breakGames.forEach(gameParser::parse);
        } finally {
            driverManager.quitDriver();
        }
    }

    private void initDriver() {
        driver = driverManager.initiateDriver(false);
        wait = new WebDriverWait(driver, 5);
    }
}
