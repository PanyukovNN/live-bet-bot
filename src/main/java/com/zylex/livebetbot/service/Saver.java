package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.SaverLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class Saver {

    private SaverLogger logger;

    private GameRepository gameRepository;

    @Autowired
    public Saver(SaverLogger logger, GameRepository gameRepository) {
        this.logger = logger;
        this.gameRepository = gameRepository;
    }

    public void save(Map<RuleNumber, List<Game>> ruleGames) {
        if (ruleGames.isEmpty()) {
            return;
        }
        logger.logRuleGames(ruleGames);
        ruleGames.forEach((k, v) -> gameRepository.save(v));
    }
}
