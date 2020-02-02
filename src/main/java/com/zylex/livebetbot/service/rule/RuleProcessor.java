package com.zylex.livebetbot.service.rule;

import com.zylex.livebetbot.model.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RuleProcessor {

    public Map<RuleNumber, List<Game>> process(List<Game> games) {
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
