package com.zylex.livebetbot.service;

import com.zylex.livebetbot.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class ResultScanner {

    private static WebDriverWait wait;

    private static WebDriver driver;

    public void process() {
        DriverManager driverManager = new DriverManager();
        driver = driverManager.initiateDriver(true);
        wait = new WebDriverWait(driver, 5);
        try {
            driver.navigate().to("http://ballchockdee.com");
            logIn();
            Thread.sleep(5000);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            driverManager.quitDriver();
        }
    }

    private void logIn() throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("LiveBetBotAuth.properties")) {
            Properties property = new Properties();
            property.load(inputStream);
            waitElementWithId("username").sendKeys(property.getProperty("LiveBetBot.login"));
            waitElementWithId("password").sendKeys(property.getProperty("LiveBetBot.password"));
            waitElementWithClassName("sign-in").click();
            driver.navigate().to(property.getProperty("logHash") + ".ballchockdee.com/ru-ru/euro/");
        }
    }

    private WebElement waitElementWithId(String id) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return driver.findElement(By.id(id));
    }

    private WebElement waitElementWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElement(By.className(className));
    }

    private List<WebElement> waitElementsWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElements(By.className(className));
    }
}
