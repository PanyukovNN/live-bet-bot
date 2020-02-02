package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.LiveBetBotException;
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
        Thread parsingTask = context.getBean(ScheduledParsingTask.class);
        Thread resultScanningTask = context.getBean(ScheduledResultScanningTask.class);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleAtFixedRate(parsingTask, 0, 10, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(resultScanningTask, 0, 1440, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(resultScanningTask, countDelay(LocalTime.of(6, 0)), 1440, TimeUnit.MINUTES);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (!reader.readLine().equalsIgnoreCase("exit")) {
            }
        } catch (IOException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            scheduler.shutdownNow();
            ConsoleLogger.endMessage(LogType.BOT_END);
        }
    }

    private static long countDelay(LocalTime time) {
        final long initialDelay = LocalDateTime.now().until(LocalDateTime.of(LocalDate.now().plusDays(1), time), ChronoUnit.MINUTES) + 1;
        if (initialDelay >= TimeUnit.DAYS.toMinutes(1)) {
            return initialDelay - 1440;
        } else {
            return initialDelay;
        }
    }
}
