package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.StatisticsCollectorLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsCollector {

    private StatisticsCollectorLogger logger = new StatisticsCollectorLogger();

    private GameRepository gameRepository;

    @Autowired
    public StatisticsCollector(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void analyse() {
        logger.startLogMessage();
        List<Game> games = gameRepository.getByDate(LocalDate.now().minusDays(1));
        for (RuleNumber ruleNumber : RuleNumber.values()) {
            List<Game> ruleGames = games.stream().filter(game -> game.getRuleNumber().equals(ruleNumber.toString())).collect(Collectors.toList());
            int twoMoreGoal = (int) ruleGames.stream().filter(game -> countTotalScore(game.getFinalScore()) > 1).count();
            int oneGoal     = (int) ruleGames.stream().filter(game -> countTotalScore(game.getFinalScore()) == 1).count();
            int noGoal      = (int) ruleGames.stream().filter(game -> countTotalScore(game.getFinalScore()) == 0).count();
            int noResult    = (int) ruleGames.stream().filter(game -> countTotalScore(game.getFinalScore()) == -2).count();
            logger.logStatistics(ruleNumber, twoMoreGoal, oneGoal, noGoal, noResult);
        }
    }

    private int countTotalScore(String score) {
        String[] scores = score.split(":");
        return Integer.parseInt(scores[0]) + Integer.parseInt(scores[1]);
    }
}
