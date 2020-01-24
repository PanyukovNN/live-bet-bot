package com.zylex.livebetbot;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.hibernate.Session;

public class ScheduledParsingTask implements Runnable {

//    private GameDao_remove gameDao;

    private DriverManager driverManager;

    private Session session;

    ScheduledParsingTask(DriverManager driverManager, Session session) {
//        this.gameDao = gameDao;
        this.driverManager = driverManager;
        this.session = session;
    }

    @Override
    public void run() {
        try {
            new Saver(
                new RuleProcessor(
                    new ParseProcessor(
                        driverManager.initiateDriver(true),
                        session
                    )),
                session
            ).save();
//            new ResultScanner(
//                driverManager.initiateDriver(true),
//                gameDao
//            ).scan();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            driverManager.quitDriver();
        }
    }
}
