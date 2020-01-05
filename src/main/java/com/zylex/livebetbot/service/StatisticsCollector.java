package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.model.Game;

import java.time.LocalDateTime;
import java.util.List;

public class StatisticsCollector {

    private GameDao gameDao;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public StatisticsCollector(GameDao gameDao, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.gameDao = gameDao;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void analyse() {
        List<Game> games = gameDao.getByDate(startDateTime, endDateTime);
        games.forEach(System.out::println);
    }
}
