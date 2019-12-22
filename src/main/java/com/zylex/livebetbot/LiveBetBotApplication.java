package com.zylex.livebetbot;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LiveBetBotApplication {

    public static void main(String[] args) throws InterruptedException {
        DriverManager driverManager = new DriverManager();
        WebDriver driver = driverManager.initiateDriver(false);
        try {
            WebDriverWait wait = new WebDriverWait(driver, 5);
            driver.navigate().to("http://ballchockdee.com/ru-ru/");
            // 1) log in - if need bets
            // 2) click on live bets
            // 3) click on football
            // 4) find all available links on countries
            Thread.sleep(5000);
        } finally {
            driverManager.quitDriver();
        }
    }
}
