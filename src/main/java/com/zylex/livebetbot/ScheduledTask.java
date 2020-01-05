package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.ResultScanner;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.rule.RuleProcessor;

@SuppressWarnings("WeakerAccess")
public class ScheduledTask implements Runnable {

    private GameDao gameDao;

    private DriverManager driverManager;

    public ScheduledTask(GameDao gameDao, DriverManager driverManager) {
        this.gameDao = gameDao;
        this.driverManager = driverManager;
    }

    @Override
    public void run() {
        try {
            new Saver(
                    new RuleProcessor(
                            new ParseProcessor(
                                    driverManager,
                                    gameDao
                            )),
                    gameDao
            ).save();
            new ResultScanner(
                    driverManager,
                    gameDao
            ).scan();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
