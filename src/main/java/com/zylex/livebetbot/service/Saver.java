package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.dao.GameDao;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;

import java.util.List;
import java.util.Map;

public class Saver {

    private RuleProcessor ruleProcessor;

    private GameDao gameDao;

    public Saver(RuleProcessor ruleProcessor, GameDao gameDao) {
        this.ruleProcessor = ruleProcessor;
        this.gameDao = gameDao;
    }

    public void save() {
        Map<RuleNumber, List<Game>> ruleGames = ruleProcessor.process();
        System.out.println();
        ruleGames.forEach((k, v) -> v.forEach(System.out::println));
        ruleGames.forEach((k, v) -> v.forEach(gameDao::save));
    }
}
