package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
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

    private SaverLogger logger = new SaverLogger();

    private GameRepository gameRepository;

    @Autowired
    public Saver(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void save(Map<RuleNumber, List<Game>> ruleGames) {
        if (ruleGames.isEmpty()) {
            ConsoleLogger.endMessage(LogType.BLOCK_END);
            return;
        }
        logger.logRuleGames(ruleGames);
        ruleGames.forEach((k, v) -> gameRepository.save(v));
    }
}
