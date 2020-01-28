package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.LiveBetBotException;
import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.repository.HibernateUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("StatementWithEmptyBody")
@ComponentScan
public class LiveBetBotApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(LiveBetBotApplication.class);
        DriverManager driverManager = context.getBean(DriverManager.class);
        driverManager.initiateDriver(true);
        Runnable parsingTask = context.getBean(ScheduledParsingTask.class);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(parsingTask, 0, 10, TimeUnit.MINUTES);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!reader.readLine().equalsIgnoreCase("exit")) {
            }
        } catch (IOException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            driverManager.quitDriver();
            HibernateUtil.getSessionFactory().getCurrentSession().close();
            scheduler.shutdownNow();
            ConsoleLogger.endMessage(LogType.BOT_END);
        }
    }

}
