package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.openqa.selenium.WebDriver;

public class ScheduledParsingTask implements Runnable {

    private GameDao gameDao;

    private WebDriver driver;

    ScheduledParsingTask(GameDao gameDao, WebDriver driver) {
        this.gameDao = gameDao;
        this.driver = driver;
    }

    @Override
    public void run() {
        try {
            new Saver(
                new RuleProcessor(
                    new ParseProcessor(
                        driver,
                        gameDao
                    )),
                gameDao
            ).save();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            ConsoleLogger.writeToLogFile();
        }
    }
}
