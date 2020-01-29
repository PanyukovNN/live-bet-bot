package com.zylex.livebetbot.service.rule;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RuleProcessor {

    private ParseProcessor parseProcessor;

    @Autowired
    public RuleProcessor(ParseProcessor parseProcessor) {
        this.parseProcessor = parseProcessor;
    }

    public Map<RuleNumber, List<Game>> process() {
        List<Game> games = parseProcessor.process();
        if (games.isEmpty()) {
            return new HashMap<>();
        }
        return filter(games);
    }

    private Map<RuleNumber, List<Game>> filter(List<Game> games) {
        Map<RuleNumber, List<Game>> ruleGames = new LinkedHashMap<>();
        for (RuleNumber ruleNumber : RuleNumber.values()) {
            ruleGames.putIfAbsent(ruleNumber, ruleNumber.rule.filter(games));
        }
        return ruleGames;
    }
}
