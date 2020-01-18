package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.rule.RuleProcessor;

public class ScheduledParsingTask implements Runnable {

    private GameDao gameDao;

    private DriverManager driverManager;

    ScheduledParsingTask(GameDao gameDao, DriverManager driverManager) {
        this.gameDao = gameDao;
        this.driverManager = driverManager;
    }

    @Override
    public void run() {
        try {
            new Saver(
                new RuleProcessor(
                    new ParseProcessor(
                        driverManager.initiateDriver(true),
                        gameDao
                    )),
                gameDao
            ).save();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            driverManager.quitDriver();
        }
    }
}
