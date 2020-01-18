package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.ResultScanner;

public class ScheduledResultScanningTask implements Runnable {

    private GameDao gameDao;

    private DriverManager driverManager;

    ScheduledResultScanningTask(GameDao gameDao, DriverManager driverManager) {
        this.gameDao = gameDao;
        this.driverManager = driverManager;
    }

    @Override
    public void run() {
        try {
            new ResultScanner(
                driverManager.initiateDriver(true),
                gameDao
            ).scan();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            driverManager.quitDriver();
        }
    }
}
