package com.zylex.livebetbot;

import com.zylex.livebetbot.exception.LiveBetBotException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.ParseProcessor;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class LiveBetBotApplication {

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            Map<RuleNumber, List<Game>> ruleGames =
                new RuleProcessor(
                    new ParseProcessor(
                        new DriverManager()
                    )).process();
            System.out.println();
            ruleGames.forEach((k, v) -> v.forEach(System.out::println));
        } catch (SQLException e) {
            throw new LiveBetBotException(e.getMessage(), e);
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
