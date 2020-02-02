package com.zylex.livebetbot.service;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class WebDriverUtil {

    private DriverManager driverManager;

    private WebDriverWait wait;

    private WebDriver driver;

    @Autowired
    public WebDriverUtil(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    @PostConstruct
    private void postConstruct() {
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, 5);
    }

    public WebElement waitElement(ByFunction byFunction, String elementName) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(byFunction.get(elementName)));
        return driver.findElement(byFunction.get(elementName));
    }

    @FunctionalInterface
    public interface ByFunction {

        By get(String input);
    }
}
