package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.service.ResultScanner;
import org.openqa.selenium.WebDriver;

public class ScheduledResultScanningTask implements Runnable {

    private GameDao gameDao;

    private WebDriver driver;

    ScheduledResultScanningTask(GameDao gameDao, WebDriver driver) {
        this.gameDao = gameDao;
        this.driver = driver;
    }

    @Override
    public void run() {
        try {
            new ResultScanner(
                driver,
                gameDao
            ).scan();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            ConsoleLogger.writeToLogFile();
        }
    }
}
