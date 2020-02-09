package com.zylex.livebetbot.service.driver;

import com.zylex.livebetbot.controller.logger.DriverConsoleLogger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Managing web drivers.
 */
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
//        driver.manage().timeouts().pageLoadTimeout(600, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
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
        return wait;
    }
}
