package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.exception.LiveBetBotException;
import com.zylex.livebetbot.service.ParseProcessor;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.rule.RuleProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class LiveBetBotApplication {

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            GameDao gameDao = new GameDao(connection);
            new Saver(
                new RuleProcessor(
                    new ParseProcessor(
                        new DriverManager(),
                        gameDao
                    )),
                gameDao
            ).save();
        } catch (SQLException e) {
            throw new LiveBetBotException(e.getMessage(), e);
        } finally {
            ConsoleLogger.writeToLogFile();
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
