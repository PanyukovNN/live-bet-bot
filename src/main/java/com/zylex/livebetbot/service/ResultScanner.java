package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ResultScannerLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResultScanner {

    private ResultScannerLogger logger = new ResultScannerLogger();

    private WebDriver driver;

    private WebDriverWait wait;

    private GameRepository gameRepository;

    private int gamesResultNumber;

    private DriverManager driverManager;

    @Autowired
    public ResultScanner(DriverManager driverManager, GameRepository gameRepository) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
    }

    public void scan() {
        try {
            int attempts = 3;
            while (attempts > 0) {
                attempts--;
                try {
                    processScanning();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    ConsoleLogger.writeErrorMessage(e.getMessage());
                }
            }
        } finally {
            driverManager.quitDriver();
        }
    }

    private void processScanning() throws IOException {
        List<Game> yesterdayGames = gameRepository.getByDate(LocalDate.now().minusDays(1));
        List<Game> noResultGames = removeGamesWithResult(yesterdayGames);
        if (noResultGames.isEmpty()) {
            createStatisticsFile();
            logger.endLogMessage(LogType.NO_GAMES, 0);
            ConsoleLogger.endMessage(LogType.BLOCK_END);
            return;
        }
        initDriver();
        logger.startLogMessage();
        String userHash = logIn();
        if (processResults(noResultGames, userHash)) {
            logger.endLogMessage(LogType.OKAY, gamesResultNumber);
        } else {
            logger.endLogMessage(LogType.ERROR, 0);
        }
        logOut();
        ConsoleLogger.endMessage(LogType.BLOCK_END);
    }

    private void createStatisticsFile() {
        if (LocalTime.now().isAfter(LocalTime.of(3, 0))) {
            if (gameRepository.createStatisticsFile(LocalDate.now().minusDays(1))) {
                logger.fileCreatedSuccessfully(LogType.OKAY);
            } else {
                logger.fileCreatedSuccessfully(LogType.ERROR);
            }
        }
    }

    private List<Game> removeGamesWithResult(List<Game> noResultGames) {
        return noResultGames.stream()
                .filter(game -> game.getFinalScore() == null ||
                        game.getFinalScore().isEmpty() ||
                        game.getFinalScore().equals("-1:-1"))
                .collect(Collectors.toList());
    }

    private boolean processResults(List<Game> noResultGames, String userHash) {
        if (navigateToResultTab(userHash)) {
            findingResults(noResultGames);
            navigateToYesterdayResultTab();
            findingResults(noResultGames);
            return true;
        }
        return false;
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
            Alert alert = (new WebDriverWait(driver, 5))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (WebDriverException ignore) {
        }
        return driver.getCurrentUrl().split("ballchockdee")[0];
    }

    private boolean navigateToResultTab(String userHash) {
        driver.navigate().to(userHash + "ballchockdee.com/web-root/restricted/result/results-more.aspx");
        int attempts = 3;
        while (true) {
            if (attempts-- == 0) {
                return false;
            }
            try {
                waitElementWithClassName("ContentTable");
                return true;
            } catch (NoSuchElementException | TimeoutException | UnhandledAlertException ignore) {
            }
        }
    }

    private void navigateToYesterdayResultTab() {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(By.name("Yesterday")));
        driver.findElement(By.name("Yesterday")).click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignore) {
        }
    }

    private void findingResults(List<Game> noResultGames) {
        noResultGames = removeGamesWithResult(noResultGames);
        if (noResultGames.isEmpty()) {
            return;
        }
        Document document = Jsoup.parse(driver.getPageSource());
        Elements gameElements = document.select("tr.tr_odd, tr.tr_even");
        for (Element gameElement : gameElements) {
            Elements cells = gameElement.select("td");
            Elements teams = cells.get(1).select("span");
            String firstTeam = teams.get(0).text();
            String secondTeam = findSecondTeam(teams);
            String score = cells.get(3).text().replace(" ", "");
            if (!checkScoreCorrectness(score)) {
                continue;
            }
            Optional<Game> gameOptional = noResultGames.stream().filter(game -> game.getFirstTeam().equals(firstTeam) && game.getSecondTeam().equals(secondTeam)).findFirst();
            if (gameOptional.isPresent()) {
                Game game = gameOptional.get();
                game.setFinalScore(score);
                gameRepository.save(game);
                gamesResultNumber++;
            }
        }
    }

    private boolean checkScoreCorrectness(String score) {
        if (score == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\d+[:]\\d+");
        return pattern.matcher(score).matches();
    }

    private String findSecondTeam(Elements teams) {
        teams.remove(0);
        for (Element teamElement : teams) {
            if (!teamElement.text().isEmpty()) {
                return teamElement.text();
            }
        }
        return "";
    }

    private void logOut() {
        driver.navigate().to("http://ballchockdee.com");
        waitElementWithClassName("sign-out").click();
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
