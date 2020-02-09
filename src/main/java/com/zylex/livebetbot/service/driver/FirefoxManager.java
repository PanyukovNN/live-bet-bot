package com.zylex.livebetbot.service.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@Primary
public class FirefoxManager extends DriverManager {

    public void initiateDriver(boolean headless) {
        quitDriver();
        WebDriverManager.firefoxdriver().setup();
        setupLogging();
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170");
        driver = headless
                ? new FirefoxDriver(options.addArguments("--headless"))
                : new FirefoxDriver();
        manageDriver();
        wait = new WebDriverWait(driver, 5);
        logger.logDriver();
    }

    private void setupLogging() {
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        logger.startLogMessage();
    }
}
