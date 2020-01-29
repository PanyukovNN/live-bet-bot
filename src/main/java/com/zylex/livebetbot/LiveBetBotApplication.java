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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("StatementWithEmptyBody")
@ComponentScan
public class LiveBetBotApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(LiveBetBotApplication.class);
        Runnable parsingTask = context.getBean(ScheduledParsingTask.class);
        Runnable resultScanningTask = context.getBean(ScheduledResultScanningTask.class);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(parsingTask, 0, 10, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(resultScanningTask, countDelay(LocalTime.of(3, 0)), 1, TimeUnit.DAYS);
        scheduler.scheduleAtFixedRate(resultScanningTask, countDelay(LocalTime.of(6, 0)), 1, TimeUnit.DAYS);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!reader.readLine().equalsIgnoreCase("exit")) {
            }
        } catch (IOException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            DriverManager driverManager = context.getBean(DriverManager.class);
            driverManager.quitDriver();
            HibernateUtil.getSessionFactory().getCurrentSession().close();
            scheduler.shutdownNow();
            ConsoleLogger.endMessage(LogType.BOT_END);
        }
    }

    private static long countDelay(LocalTime time) {
        final long initialDelay = LocalDateTime.now().until(LocalDateTime.of(LocalDate.now().plusDays(1), time), ChronoUnit.MINUTES);
        if (initialDelay > TimeUnit.DAYS.toMinutes(1)) {
            return LocalDateTime.now().until(LocalDateTime.of(LocalDate.now(), time), ChronoUnit.MINUTES);
        } else {
            return initialDelay;
        }
    }
}
