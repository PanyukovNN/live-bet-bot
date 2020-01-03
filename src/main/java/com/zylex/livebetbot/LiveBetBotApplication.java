package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.LiveBetBotException;
import com.zylex.livebetbot.service.DriverManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LiveBetBotApplication {

    public static void main(String[] args) {
        DriverManager driverManager = new DriverManager();
        driverManager.initiateDriver(true);
        try (Connection connection = getConnection()) {
            ScheduledTask st = new ScheduledTask(
                    new GameDao(connection),
                    driverManager
            );
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(st, 0, 10, TimeUnit.MINUTES);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //noinspection StatementWithEmptyBody
            while (!reader.readLine().equals("exit")) {
            }
        } catch (SQLException | IOException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            driverManager.quitDriver();
            ConsoleLogger.endMessage(LogType.BOT_END);
            ConsoleLogger.writeToLogFile();
            System.exit(0);
        }
    }

    private static Connection getConnection() {
        try(InputStream inputStream = LiveBetBotApplication.class.getClassLoader().getResourceAsStream("LiveBetBotDb.properties")) {
            Properties property = new Properties();
            property.load(inputStream);
            final String login = property.getProperty("db.login");
            final String password = property.getProperty("db.password");
            final String url = property.getProperty("db.url");
            Class.forName("org.postgresql.Driver");
            return java.sql.DriverManager.getConnection(url, login, password);
        } catch(SQLException | IOException | ClassNotFoundException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        }
    }
}
