package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.exception.LiveBetBotException;
import com.zylex.livebetbot.exception.StatisticsApplicationException;
import com.zylex.livebetbot.service.StatisticsCollector;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class StatisticsApplication {

    public static void main(String[] args) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startDateTime = args.length > 0
                ? LocalDateTime.of(LocalDate.parse(args[0], DATE_FORMATTER), LocalTime.MIN)
                : LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDateTime = args.length > 1
                ? LocalDateTime.of(LocalDate.parse(args[1], DATE_FORMATTER), LocalTime.MAX)
                : LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.MAX);
        try (Connection connection = getConnection()) {
            new StatisticsCollector(
                    new GameDao(connection),
                    startDateTime,
                    endDateTime
            ).analyse();
        } catch (SQLException e) {
            throw new StatisticsApplicationException(e.getMessage(), e);
        }
    }

    private static Connection getConnection() {
        try(InputStream inputStream = StatisticsApplication.class.getClassLoader().getResourceAsStream("dataBase.properties")) {
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
