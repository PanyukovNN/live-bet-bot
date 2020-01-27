package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.SaverLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.repository.GameRepository;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;

import java.util.Map;
import java.util.Set;

public class Saver {

    private SaverLogger logger = new SaverLogger();

    private RuleProcessor ruleProcessor;

    private GameRepository gameRepository;

    public Saver(RuleProcessor ruleProcessor, GameRepository gameRepository) {
        this.ruleProcessor = ruleProcessor;
        this.gameRepository = gameRepository;
    }

    public void save() {
        Map<RuleNumber, Set<Game>> ruleGames = ruleProcessor.process();
        if (ruleGames.isEmpty()) {
            ConsoleLogger.endMessage(LogType.BLOCK_END);
            return;
        }
        logger.logRuleGames(ruleGames);
        ruleGames.forEach((k, v) -> gameRepository.save(v));
    }
}
