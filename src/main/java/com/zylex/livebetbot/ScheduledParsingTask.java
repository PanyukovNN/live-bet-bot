package com.zylex.livebetbot;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.HibernateUtil;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.hibernate.Session;

public class ScheduledParsingTask implements Runnable {

    @Override
    public void run() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            GameRepository gameRepository = new GameRepository(session);
            new Saver(
                new RuleProcessor(
                    new ParseProcessor(
                        new DriverManager(),
                        gameRepository
                    )),
                gameRepository
            ).save();
//            new ResultScanner(
//                driverManager.initiateDriver(true),
//                gameRepository
//            ).scan();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
