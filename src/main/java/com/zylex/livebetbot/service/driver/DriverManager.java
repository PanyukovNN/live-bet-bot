package com.zylex.livebetbot.service.driver;

import com.zylex.livebetbot.controller.logger.DriverConsoleLogger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Managing web drivers.
 */
@SuppressWarnings("WeakerAccess")
@Service
public abstract class DriverManager {

    protected static final DriverConsoleLogger logger = new DriverConsoleLogger();

    protected WebDriver driver;

    protected WebDriverWait wait;

    public WebDriver getDriver() {
        return driver;
    }

    @PreDestroy
    private void preDestroy() {
        quitDriver();
    }

    /**
     * Initiate web driver and return it.
     * @param headless - flag for headless driver.
     */
    public abstract void initiateDriver(boolean headless);

    protected void manageDriver() {
        driver.manage().window().setSize(new Dimension(1024, 768));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
    }

    /**
     * Quit driver if it was initiated.
     */
    protected void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public void refreshDriver() {
        initiateDriver(true);
    }

    public WebDriverWait getWait() {
        return this.wait;
    }

    public WebElement waitElement(Function<String, By> byFunction, String elementName) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(byFunction.apply(elementName)));
        return driver.findElement(byFunction.apply(elementName));
    }
}
