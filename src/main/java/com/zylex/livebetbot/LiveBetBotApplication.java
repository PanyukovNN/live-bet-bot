package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.exception.LiveBetBotException;
import com.zylex.livebetbot.service.DriverManager;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.*;

public class LiveBetBotApplication {

    public static void main(String[] args) {
//        DriverManager parsingDriverManager = new DriverManager();
//        DriverManager resultScannerDriverManager = new DriverManager();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        try (Connection connection = getConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            ScheduledParsingTask parsingTask = new ScheduledParsingTask(
                    new GameDao(connection),
                    new DriverManager()
            );
            ScheduledResultScanningTask resultScanningTask = new ScheduledResultScanningTask(
                    new GameDao(connection),
                    new DriverManager()
            );
            scheduler.scheduleAtFixedRate(parsingTask, 0, 10, TimeUnit.MINUTES);
            scheduler.scheduleAtFixedRate(resultScanningTask, 0, 120, TimeUnit.MINUTES);
            while (!reader.readLine().equalsIgnoreCase("exit")) {
            }
        } catch (SQLException | IOException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            scheduler.shutdown();
//            parsingDriverManager.quitDriver();
//            resultScannerDriverManager.quitDriver();
            ConsoleLogger.endMessage(LogType.BOT_END);
        }
    }

    private static Connection getConnection() {
        try(InputStream inputStream = LiveBetBotApplication.class.getClassLoader().getResourceAsStream("dataBase.properties")) {
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
