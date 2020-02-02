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

    private WebDriverUtil webDriverUtil;

    private WebDriver driver;

    private GameRepository gameRepository;

    private int gamesResultNumber;

    private DriverManager driverManager;

    @Autowired
    public ResultScanner(DriverManager driverManager, GameRepository gameRepository, WebDriverUtil webDriverUtil) {
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
        this.webDriverUtil = webDriverUtil;
    }

    public void scan() {
        int attempts = 3;
        while (attempts-- > 0) {
            try {
                processScanning();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                ConsoleLogger.writeErrorMessage(e.getMessage());
            }
        }
    }

    private void processScanning() throws IOException {
        driver = driverManager.getDriver();
        logger.startLogMessage();
        List<Game> noResultGames = findNoResultGames();
        if (noResultGames.isEmpty()) {
            return;
        }
        String userHash = logIn();
        //TODO add to userHash
//        http://t0x313uroa0e.asia.ballchockdee.com/web-root/restricted/default.aspx?loginname=543cb1d8ce3a9087c1c86c51f512a08d
        processResults(noResultGames, userHash);
        logOut(userHash);
        ConsoleLogger.endMessage(LogType.BLOCK_END);
    }

    private List<Game> findNoResultGames() {
        List<Game> yesterdayGames = gameRepository.getByDate(LocalDate.now().minusDays(1));
        List<Game> noResultGames = removeGamesWithResult(yesterdayGames);
        if (noResultGames.isEmpty()) {
            createStatisticsFile();
            logger.endLogMessage(LogType.NO_GAMES, 0);
            ConsoleLogger.endMessage(LogType.BLOCK_END);
        } else {
            logger.noResultGamesFound(noResultGames.size());
        }
        return noResultGames;
    }

    private void createStatisticsFile() {
        if (LocalTime.now().isAfter(LocalTime.of(2, 59))) {
            if (gameRepository.createStatisticsFile(LocalDate.now().minusDays(1))) {
                logger.fileCreatedSuccessfully(LogType.OKAY);
            } else {
                logger.fileCreatedSuccessfully(LogType.FILE_EXISTS);
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

    private void processResults(List<Game> noResultGames, String userHash) {
        if (navigateToResultTab(userHash)) {
            findingResults(noResultGames);
            navigateToYesterdayResultTab();
            findingResults(noResultGames);
            logger.endLogMessage(LogType.OKAY, gamesResultNumber);
        } else {
            logger.endLogMessage(LogType.ERROR, 0);
        }
    }

    private String logIn() throws IOException {
        driver.navigate().to("http://ballchockdee.com");
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("LiveBetBotAuth.properties")) {
            Properties property = new Properties();
            property.load(inputStream);
            webDriverUtil.waitElement(By::id, "username").sendKeys(property.getProperty("LiveBetBot.login"));
            webDriverUtil.waitElement(By::id, "password").sendKeys(property.getProperty("LiveBetBot.password"));
            webDriverUtil.waitElement(By::className, "sign-in").click();
            if (driver.getCurrentUrl().startsWith("https://www.sbobet-pay.com/")) {
                webDriverUtil.waitElement(By::className, "DWHomeBtn").click();
            }
            Alert alert = (new WebDriverWait(driver, 5))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
            logger.logIn(LogType.OKAY);
        } catch (WebDriverException ignore) {
            logger.logIn(LogType.ERROR);
        }
        return driver.getCurrentUrl().split("ballchockdee")[0];
    }

    private boolean navigateToResultTab(String userHash) {
        int attempts = 3;
        while (attempts-- > 0) {
            try {
                driver.navigate().to(userHash + "ballchockdee.com/web-root/restricted/result/results-more.aspx");
                webDriverUtil.waitElement(By::className, "ContentTable");
                return true;
            } catch (NoSuchElementException | TimeoutException | UnhandledAlertException ignore) {
            }
        }
        return false;
    }

    private void navigateToYesterdayResultTab() {
        webDriverUtil.waitElement(By::name, "Yesterday").click();
        while (true) {
            try {
                String day = driver.findElement(By.id("fromdate")).getAttribute("value").split("/")[1];
                if (Integer.parseInt(day) == LocalDate.now().minusDays(1).getDayOfMonth()) {
                    break;
                } else {
                    Thread.sleep(300);
                }
            } catch (InterruptedException ignore) {
            }
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

    private void logOut(String userHash) {
        try {
            driver.navigate().to(userHash + "ballchockdee.com/");
            webDriverUtil.waitElement(By::className, "sign-out")
                    .findElement(By.tagName("a")).click();
            logger.logOut(LogType.OKAY);
        } catch (WebDriverException e) {
            logger.logOut(LogType.ERROR);
            ConsoleLogger.writeErrorMessage(e.getMessage());
        }
    }
}
