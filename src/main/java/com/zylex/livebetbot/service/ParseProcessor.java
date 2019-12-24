package com.zylex.livebetbot.service;

import com.zylex.livebetbot.DriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseProcessor {

    private static WebDriverWait wait;

    private static WebDriver driver;

    public void process() {
        DriverManager driverManager = new DriverManager();
        driver = driverManager.initiateDriver(false);
        wait = new WebDriverWait(driver, 5);
        try {
            List<String> countryLinks = findCountryLinks();

            driver.navigate().to("http://ballchockdee.com" + countryLinks.get(1));
            Document document = Jsoup.parse(driver.getPageSource());

            waitElementWithId("bu:od:go:mt:2").click();

            Elements markets = document.select("table.Hdp > tbody > tr");
            for (Element market : markets) {
                Element dateTimeText = market.selectFirst("div.DateTimeTxt");
    //                if (dateTimeText.text().contains("Перерыв")) {
    //                    System.out.println("fuck yeah");
    //                }
            }
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
            driverManager.quitDriver();
        }
    }

    private List<String> findCountryLinks() throws IOException {
        List<String> countryLinks = new ArrayList<>();
        Document document = Jsoup.connect("http://ballchockdee.com/ru-ru/euro/ставки-live/футбол")
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elements = document.select("ul#ms-live-res-ul-1 > li.Unsel > a");
        elements.forEach(element -> countryLinks.add(element.attr("href")));
        return countryLinks;
    }

    private WebElement waitElementWithId(String id) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
        return driver.findElement(By.id(id));
    }

    private List<WebElement> waitElementsWithClassName(String className) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
        return driver.findElements(By.className(className));
    }
}
