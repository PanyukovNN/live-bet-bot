package com.zylex.livebetbot.service.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ChromeManager extends DriverManager {

    public void initiateDriver(boolean headless) {
        quitDriver();
        WebDriverManager.chromedriver().setup();
        setupLogging();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170");
        driver = headless
                ? new ChromeDriver(options.addArguments("--headless"))
                : new ChromeDriver();
        manageDriver();
        wait = new WebDriverWait(driver, 5);
        logger.logDriver();
    }

    private void setupLogging() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        logger.startLogMessage();
    }
}
