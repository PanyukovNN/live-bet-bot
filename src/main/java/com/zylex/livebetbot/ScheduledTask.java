package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.ParseProcessor;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.rule.RuleProcessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("WeakerAccess")
public class ScheduledTask implements Runnable {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm a");

    private GameDao gameDao;

    private DriverManager driverManager;

    public ScheduledTask(GameDao gameDao, DriverManager driverManager) {
        this.gameDao = gameDao;
        this.driverManager = driverManager;
    }

    @Override
    public void run() {
        new Saver(
            new RuleProcessor(
                new ParseProcessor(
                    driverManager,
                    gameDao
                )),
            gameDao
        ).save();
    }
}
