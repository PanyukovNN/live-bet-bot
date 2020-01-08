package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.exception.StatisticsApplicationException;
import com.zylex.livebetbot.service.StatisticsCollector;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class StatisticsApplication {

    public static void main(String[] args) {
        LocalDateTime startDateTime = LocalDateTime.of(
                LocalDate.of(2020, 1, 7),
                LocalTime.of(0, 0));
        LocalDateTime endDateTime = LocalDateTime.now();

        try (Connection connection = LiveBetBotApplication.getConnection()) {
            new StatisticsCollector(
                    new GameDao(connection),
                    startDateTime,
                    endDateTime
            ).analyse();
        } catch (SQLException e) {
            throw new StatisticsApplicationException(e.getMessage(), e);
        }
    }
}
