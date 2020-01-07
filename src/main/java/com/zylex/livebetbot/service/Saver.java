package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.controller.logger.SaverLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;

import java.util.List;
import java.util.Map;

public class Saver {

    private SaverLogger logger = new SaverLogger();

    private RuleProcessor ruleProcessor;

    private GameDao gameDao;

    public Saver(RuleProcessor ruleProcessor, GameDao gameDao) {
        this.ruleProcessor = ruleProcessor;
        this.gameDao = gameDao;
    }

    public void save() {
        Map<RuleNumber, List<Game>> ruleGames = ruleProcessor.process();
        if (ruleGames.isEmpty()) {
            logger.logBlockEndSeparator();
            return;
        }
        logger.logRuleGames(ruleGames);
        ruleGames.forEach((k, v) -> v.forEach(gameDao::save));
    }
}
