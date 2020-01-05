package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        for (RuleNumber ruleNumber : RuleNumber.values()) {
            List<Game> ruleGames = games.stream().filter(game -> game.getRuleNumber() == ruleNumber).collect(Collectors.toList());
            int moreGoal = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) > 1).count();
            int oneGoal = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) == 1).count();
            int noGoal = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) == 0).count();
            int noResult = (int) ruleGames.stream().filter(game -> (game.getFinalGoal().getHomeGoals() + game.getFinalGoal().getAwayGoals()) == -2).count();
            logStatistics(ruleNumber, moreGoal, oneGoal, noGoal, noResult);
        }
    }

    private void logStatistics(RuleNumber ruleNumber, int moreGoal, int oneGoal, int noGoal, int noResult) {
        System.out.print("\nGames for " + ruleNumber + ":");
        System.out.print(String.format("\n%12s%3d", "More goal - ", moreGoal));
        System.out.print(String.format("\n%12s%3d", "One goal - ", oneGoal));
        System.out.print(String.format("\n%12s%3d", "No goal - ", noGoal));
        if (noResult > 0) {
            System.out.print(String.format("\n%12s%3d", "N/R - ", noResult));
        }
        System.out.print("\n" + StringUtils.repeat("-", 50));
    }
}
