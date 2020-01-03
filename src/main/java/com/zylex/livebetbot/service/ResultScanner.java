package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ResultScannerLogger;
import com.zylex.livebetbot.exception.ResultScannerException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.Goal;
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
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ResultScanner {

    private ResultScannerLogger logger = new ResultScannerLogger();

    private WebDriverWait wait;

    private WebDriver driver;

    private DriverManager driverManager;

    private GameDao gameDao;

    public ResultScanner(DriverManager driverManager, GameDao gameDao) {
        this.driverManager = driverManager;
        this.gameDao = gameDao;
    }

    public void scan() {
        try {
            logger.startLogMessage();
            List<Game> noResultGames = gameDao.getNoResultGames();
            if (!noResultGames.isEmpty()) {
                initDriver();
                String userHash = logIn();
                processResults(noResultGames, userHash);
            }
        } catch (IOException e) {
            throw new ResultScannerException(e.getMessage(), e);
        } finally {
            logger.endLogMessage();
            ConsoleLogger.endMessage(LogType.BLOCK_END);
        }
    }

    private void processResults(List<Game> noResultGames, String userHash) {
        navigateToResultTab(userHash);
        findingResults(noResultGames);
        navigateToYesterdayResultTab();
        findingResults(noResultGames);
    }

    private void initDriver() {
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, 5);
        driver.navigate().to("http://ballchockdee.com");
    }

    private String logIn() throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("LiveBetBotAuth.properties")) {
            Properties property = new Properties();
            property.load(inputStream);
            waitElementWithId("username").sendKeys(property.getProperty("LiveBetBot.login"));
            waitElementWithId("password").sendKeys(property.getProperty("LiveBetBot.password"));
            waitElementWithClassName("sign-in").click();
            if (driver.getCurrentUrl().startsWith("https://www.sbobet-pay.com/")) {
                waitElementWithClassName("DWHomeBtn").click();
            }
            return driver.getCurrentUrl().split("ballchockdee")[0];
        }
    }

    private void navigateToResultTab(String userHash) {
        driver.navigate().to(userHash + "ballchockdee.com/web-root/restricted/result/results-more.aspx");
        waitElementWithClassName("ContentTable");
    }

    private void navigateToYesterdayResultTab() {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.name("Yesterday")));
        driver.findElement(By.name("Yesterday")).click();
        waitElementWithClassName("ContentTable");
    }

    private void findingResults(List<Game> noResultGames) {
        if (noResultGames.isEmpty()) {
            return;
        }
        noResultGames = removeGameWithResults(noResultGames);
        Document document = Jsoup.parse(driver.getPageSource());
        Elements gameElements = document.select("table.ContentTable > tbody > tr.tr_odd, table.ContentTable > tbody > tr.tr_even");
        for (Element gameElement : gameElements) {
            Elements cells = gameElement.select("td");
            Elements teams = cells.get(1).select("span");
            String firstTeam = teams.get(0).text();
            int t = teams.size() > 2 ? 1 : 0;
            String secondTeam = teams.get(1 + t).text();
            String[] finalScores = cells.get(3).text().split(" : ");
            if (finalScores[0].equals("-")) {
                continue;
            }
            for (Game game : noResultGames) {
                if (game.getFinalGoal().getHomeGoals() >= 0) {
                    continue;
                }
                if (game.getFirstTeam().equals(firstTeam)  && game.getSecondTeam().equals(secondTeam)) {
                    int homeGoalFinal = Integer.parseInt(finalScores[0]);
                    int awayGoalFinal = Integer.parseInt(finalScores[1]);
                    game.setFinalGoal(new Goal(homeGoalFinal, awayGoalFinal));
                    gameDao.save(game);
                }
            }
        }
    }

    private List<Game> removeGameWithResults(List<Game> noResultGames) {
        return noResultGames.stream().filter(game -> game.getFinalGoal().getHomeGoals() == -1).collect(Collectors.toList());
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
}
