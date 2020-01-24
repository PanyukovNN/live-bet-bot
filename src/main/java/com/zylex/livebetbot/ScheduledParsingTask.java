package com.zylex.livebetbot;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.ResultScanner;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.hibernate.Session;

public class ScheduledParsingTask implements Runnable {

    private DriverManager driverManager;

    private Session session;

    ScheduledParsingTask(DriverManager driverManager, Session session) {
        this.driverManager = driverManager;
        this.session = session;
    }

    @Override
    public void run() {
        GameRepository gameRepository = new GameRepository(session);
        try {
            new Saver(
                new RuleProcessor(
                    new ParseProcessor(
                        driverManager.initiateDriver(true),
                        gameRepository
                    )),
                gameRepository
            ).save();
            new ResultScanner(
                driverManager.initiateDriver(true),
                gameRepository
            ).scan();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            driverManager.quitDriver();
        }
    }
}
