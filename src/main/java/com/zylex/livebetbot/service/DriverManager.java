package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.DriverConsoleLogger;
import com.zylex.livebetbot.service.util.AttemptsUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Managing web drivers.
 */
@Service
public class DriverManager {

    private DriverConsoleLogger logger = new DriverConsoleLogger();

    private WebDriver driver;

    private WebDriverWait wait;

    public WebDriver getDriver() {
        return driver;
    }

    @PostConstruct
    private void postConstruct() {
        AttemptsUtil.attempt(this::initiateDriver, true, 3);
    }

    @PreDestroy
    private void preDestroy() {
        quitDriver();
    }

    /**
     * Initiate web driver and return it.
     * @param headless - flag for headless driver.
     */
    private void initiateDriver(boolean headless) {
        quitDriver();
        WebDriverManager.firefoxdriver().setup();
        setUpLogging();
        driver = headless
                ? new FirefoxDriver(new FirefoxOptions().addArguments("--headless"))
                : new FirefoxDriver();
        manageDriver();
        wait = new WebDriverWait(driver, 5);
        logger.logDriver();
    }

    private void manageDriver() {
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
    }

    private void setUpLogging() {
//        System.setProperty("webdriver.chrome.silentOutput", "true");
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        logger.startLogMessage();
    }

    /**
     * Quit driver if it was initiated.
     */
    private void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public void refreshDriver() {
        initiateDriver(true);
    }

    public Optional<WebElement> waitElement(ByFunction byFunction, String elementName) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(byFunction.get(elementName)));
        return Optional.of(driver.findElement(byFunction.get(elementName)));
    }

    @FunctionalInterface
    public interface ByFunction {
        By get(String input);
    }
}
