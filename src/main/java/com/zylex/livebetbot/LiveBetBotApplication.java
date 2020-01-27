package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.LiveBetBotException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("StatementWithEmptyBody")
public class LiveBetBotApplication {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            scheduler.scheduleAtFixedRate(new ScheduledParsingTask(), 0, 10, TimeUnit.MINUTES);
            while (!reader.readLine().equalsIgnoreCase("exit")) {
            }
        } catch (IOException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            scheduler.shutdown();
            ConsoleLogger.endMessage(LogType.BOT_END);
        }
    }

}
