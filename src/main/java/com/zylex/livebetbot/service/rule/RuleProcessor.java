package com.zylex.livebetbot.service.rule;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.parser.ParseProcessor;

import java.util.*;

public class RuleProcessor {

    private ParseProcessor parseProcessor;

    public RuleProcessor(ParseProcessor parseProcessor) {
        this.parseProcessor = parseProcessor;
    }

    public Map<RuleNumber, Set<Game>> process() {
        List<Game> games = parseProcessor.process();
        if (games.isEmpty()) {
            return new HashMap<>();
        }
        return filter(games);
    }

    private Map<RuleNumber, Set<Game>> filter(List<Game> games) {
        Map<RuleNumber, Set<Game>> ruleGames = new LinkedHashMap<>();
        for (RuleNumber ruleNumber : RuleNumber.values()) {
            ruleGames.putIfAbsent(ruleNumber, ruleNumber.rule.filter(games));
        }
        return ruleGames;
    }
}
