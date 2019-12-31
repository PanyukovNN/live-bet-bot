package com.zylex.livebetbot.service.rule;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.ParseProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleProcessor {

    private ParseProcessor parseProcessor;

    public RuleProcessor(ParseProcessor parseProcessor) {
        this.parseProcessor = parseProcessor;
    }

    public Map<RuleNumber, List<Game>> process() {
        List<Game> games = parseProcessor.process();
        return filter(games);
    }

    private Map<RuleNumber, List<Game>> filter(List<Game> games) {
        Map<RuleNumber, List<Game>> ruleGames = new HashMap<>();
        for (RuleNumber ruleNumber : RuleNumber.values()) {
            ruleGames.putIfAbsent(ruleNumber, ruleNumber.rule.filter(games));
        }
        return ruleGames;
    }
}