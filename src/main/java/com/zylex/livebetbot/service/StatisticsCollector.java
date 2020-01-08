package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.StatisticsCollectorLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsCollector {

    private StatisticsCollectorLogger logger = new StatisticsCollectorLogger();

    private GameDao gameDao;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public StatisticsCollector(GameDao gameDao, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.gameDao = gameDao;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void analyse() {
        logger.startLogMessage(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        List<Game> games = gameDao.getByDate(startDateTime, endDateTime);
        for (RuleNumber ruleNumber : RuleNumber.values()) {
            List<Game> ruleGames = games.stream().filter(game -> game.getRuleNumber() == ruleNumber).collect(Collectors.toList());
            int overGoal    = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) > 1).count();
            int refund      = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) == 1).count();
            int noGoal      = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) == 0).count();
            int noResult    = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) == -2).count();
            logger.logStatistics(ruleNumber, overGoal, refund, noGoal, noResult);
        }
        int insertedGames = gameDao.createStatisticsFile(startDateTime, endDateTime);
        logger.fileCreatedSuccessfully(insertedGames);
    }
}
