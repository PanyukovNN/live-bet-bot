package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.DriverConsoleLogger;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Managing chrome drivers.
 */
@SuppressWarnings("WeakerAccess")
public class DriverManager {

    private DriverConsoleLogger logger = new DriverConsoleLogger();

    private WebDriver driver;

    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Initiate web driver and return it.
     * @param headless - flag for headless driver.
     */
    public void initiateDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        setUpLogging();
        driver = headless
                ? new ChromeDriver(new ChromeOptions().addArguments("--headless"))
                : new ChromeDriver();
        manageDriver();
        logger.logDriver();
    }

    private void manageDriver() {
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
    }

    private void setUpLogging() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        logger.startLogMessage();
    }

    /**
     * Quit driver if it was initiated.
     */
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
