package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.ResultScannerLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.util.AttemptsUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
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

    private void processScanning() {
        driver = driverManager.getDriver();
        wait = new WebDriverWait(driver, 5);
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
            createStatisticsFiles(LocalDate.now().minusDays(1));
            createStatisticsFiles(LocalDate.now());
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

    private void createStatisticsFiles(LocalDate date) {
        if (gameRepository.createStatisticsFile(date)) {
            logger.fileCreatedSuccessfully(LogType.OKAY, date);
        } else {
            logger.fileCreatedSuccessfully(LogType.NO_GAMES, date);
        }
    }

    private String logIn() {
        driver.navigate().to("http://ballchockdee.com");
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("LiveBetBotAuth.properties")) {
            Properties property = new Properties();
            property.load(inputStream);
            waitElement(By::id, "username").sendKeys(property.getProperty("LiveBetBot.login"));
            waitElement(By::id, "password").sendKeys(property.getProperty("LiveBetBot.password"));
            waitElement(By::className, "sign-in").click();
            if (driver.getCurrentUrl().startsWith("https://www.sbobet-pay.com/")) {
                waitElement(By::className, "DWHomeBtn").click();
            }
            logger.logIn(LogType.OKAY);
            try {
                Alert alert = (new WebDriverWait(driver, 2))
                        .until(ExpectedConditions.alertIsPresent());
                alert.accept();
            } catch (WebDriverException ignore) {
            }
        } catch (Exception e) {
            logger.logIn(LogType.ERROR);
        }
        return driver.getCurrentUrl();
    }

    private void processResults(List<Game> noResultGames, String userHash) {
        if (AttemptsUtil.attempt(this::navigateToResultTab, userHash, 3)) {
            findingResults(noResultGames);
            navigateToYesterdayResultTab();
            findingResults(noResultGames);
            logger.endLogMessage(LogType.OKAY, gamesResultNumber);
        } else {
            logger.endLogMessage(LogType.ERROR, 0);
        }
    }

    private void navigateToResultTab(String userHash) {
        driver.navigate().to(userHash + "ballchockdee.com/web-root/restricted/result/results-more.aspx");
        waitElement(By::className, "ContentTable");
    }

    private void navigateToYesterdayResultTab() {
        waitElement(By::name, "Yesterday");
        Consumer<Object> consumer = (obj) -> {
            try {
                String day = driver.findElement(By.id("fromdate")).getAttribute("value").split("/")[1];
                if (Integer.parseInt(day) != LocalDate.now().minusDays(1).getDayOfMonth()) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ignore) {
            }
        };
        AttemptsUtil.attempt(consumer, 0, 2);
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
                gameRepository.update(game);
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

    private void logOut(String userHomeLink) {
        try {
            driver.navigate().to(userHomeLink);
            waitElement(By::className, "sign-out").findElement(By.tagName("a")).click();
            logger.logOut(LogType.OKAY);
        } catch (WebDriverException e) {
            logger.logOut(LogType.ERROR);
            ConsoleLogger.writeErrorMessage(e.getMessage());
        }
    }

    private WebElement waitElement(ByFunction byFunction, String elementName) {
        wait.ignoring(StaleElementReferenceException.class)
                .until(ExpectedConditions.presenceOfElementLocated(byFunction.get(elementName)));
        return driver.findElement(byFunction.get(elementName));
    }

    @FunctionalInterface
    public interface ByFunction {
        By get(String input);
    }
}
