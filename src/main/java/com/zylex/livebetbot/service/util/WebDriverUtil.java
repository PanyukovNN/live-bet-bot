package com.zylex.livebetbot.service.util;

import com.zylex.livebetbot.service.DriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

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

    public Optional<WebElement> waitElement(ByFunction byFunction, String elementName) {
        try {
            wait.ignoring(StaleElementReferenceException.class)
                    .until(ExpectedConditions.presenceOfElementLocated(byFunction.get(elementName)));
            return Optional.of(driver.findElement(byFunction.get(elementName)));
        } catch (WebDriverException e) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    public interface ByFunction {

        By get(String input);
    }
}
