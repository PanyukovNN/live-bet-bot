package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ResultScannerLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.driver.DriverManager;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResultScanner {

    private ResultScannerLogger logger;

    private GameRepository gameRepository;

    private DriverManager driverManager;

    @Autowired
    public ResultScanner(ResultScannerLogger logger, DriverManager driverManager, GameRepository gameRepository) {
        this.logger = logger;
        this.driverManager = driverManager;
        this.gameRepository = gameRepository;
    }

    public void scan() {
        int attempts = 3;
        while (attempts-- > 0) {
            try {
                processScanning();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                ConsoleLogger.writeErrorMessage(e.getMessage(), e);
            }
        }
    }

    private void processScanning() {
        logger.startLogMessage();
        List<Game> noResultGames = findNoResultGames();
        if (noResultGames.isEmpty()) {
            return;
        }
        String userHomeLink = logIn();
        String userHash = userHomeLink.split("ballchockdee")[0];
        processResults(noResultGames, userHash);
//        logOut(userHomeLink);
        ConsoleLogger.endMessage(LogType.BLOCK_END);
    }

    private List<Game> findNoResultGames() {
        List<Game> sinceYesterdayGames = gameRepository.getFromDate(LocalDate.now().minusDays(1));
        List<Game> noResultGames = removeGamesWithResult(sinceYesterdayGames);
        noResultGames = removeEarlyGames(noResultGames);
        if (noResultGames.isEmpty()) {
//            createStatisticsFiles(LocalDate.now().minusDays(1));
//            createStatisticsFiles(LocalDate.now());
            logger.endLogMessage(LogType.NO_GAMES, 0);
            ConsoleLogger.endMessage(LogType.BLOCK_END);
        } else {
            logger.noResultGamesFound(noResultGames.size());
        }
        return noResultGames;
    }

    private List<Game> removeGamesWithResult(List<Game> noResultGames) {
        return noResultGames.stream()
                .filter(game -> game.getFinalScore() == null ||
                        game.getFinalScore().isEmpty() ||
                        game.getFinalScore().equals("-1:-1"))
                .collect(Collectors.toList());
    }

    private List<Game> removeEarlyGames(List<Game> games) {
        return games.stream()
                .filter(game -> game.getDateTime().isBefore(LocalDateTime.now().minusHours(1)))
                .collect(Collectors.toList());
    }

//    private void createStatisticsFiles(LocalDate date) {
//        if (gameRepository.createStatisticsFile(date)) {
//            logger.fileCreatedSuccessfully(LogType.OKAY, date);
//        } else {
//            logger.fileCreatedSuccessfully(LogType.NO_GAMES, date);
//        }
//    }

    private String logIn() {
        driverManager.getDriver().navigate().to("http://ballchockdee.com");
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("LiveBetBotAuth.properties")) {
            Properties property = new Properties();
            property.load(inputStream);
            driverManager.waitElement(By::id, "username").sendKeys(property.getProperty("LiveBetBot.login"));
            driverManager.waitElement(By::id, "password").sendKeys(property.getProperty("LiveBetBot.password"));
            driverManager.waitElement(By::className, "sign-in").click();
            if (driverManager.getDriver().getCurrentUrl().startsWith("https://www.sbobet-pay.com/")) {
                driverManager.waitElement(By::className, "DWHomeBtn").click();
            }
            logger.logIn(LogType.OKAY);
            try {
                Alert alert = (driverManager.getWait()).until(ExpectedConditions.alertIsPresent());
                alert.accept();
            } catch (WebDriverException ignore) {
            }
        } catch (Exception e) {
            logger.logIn(LogType.ERROR);
        }
        return driverManager.getDriver().getCurrentUrl();
    }

    private void processResults(List<Game> noResultGames, String userHash) {
        try {
            navigateToResultTab(userHash);
            int resultsNumber = findingResults(noResultGames);
            navigateToYesterdayResultTab();
            resultsNumber += findingResults(noResultGames);
            logger.endLogMessage(LogType.OKAY, resultsNumber);
        } catch (Exception e) {
            logger.endLogMessage(LogType.ERROR, 0);
        }
    }

    private void navigateToResultTab(String userHash) {
        driverManager.getDriver().navigate().to(userHash + "ballchockdee.com/web-root/restricted/result/results-more.aspx");
        driverManager.waitElement(By::className, "ContentTable");
    }

    private void navigateToYesterdayResultTab() {
        driverManager.waitElement(By::name, "Yesterday");
        int attempts = 2;
        while (attempts-- > 0) {
            try {
                String day = driverManager.getDriver().findElement(By.id("fromdate")).getAttribute("value").split("/")[1];
                if (Integer.parseInt(day) != LocalDate.now().minusDays(1).getDayOfMonth()) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ignore) {
            }
        }
    }

    private int findingResults(List<Game> noResultGames) {
        int resultsNumber = 0;
        noResultGames = removeGamesWithResult(noResultGames);
        if (noResultGames.isEmpty()) {
            return resultsNumber;
        }
        Document document = Jsoup.parse(driverManager.getDriver().getPageSource());
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
                gameRepository.update(game);
                resultsNumber++;
            }
        }
        return resultsNumber;
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

    private void logOut(String userHomeLink) {
        try {
            driverManager.getDriver().navigate().to(userHomeLink);
            driverManager.waitElement(By::className, "sign-out").findElement(By.tagName("a")).click();
            logger.logOut(LogType.OKAY);
        } catch (WebDriverException e) {
            logger.logOut(LogType.ERROR);
            ConsoleLogger.writeErrorMessage(e.getMessage(), e);
        }
    }
}
