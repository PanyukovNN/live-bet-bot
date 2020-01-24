package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;

import java.util.Map;
import java.util.Set;

public class SaverLogger extends ConsoleLogger {

    public void logRuleGames(Map<RuleNumber, Set<Game>> ruleGames) {
        for (Map.Entry<RuleNumber, Set<Game>> entry : ruleGames.entrySet()) {
            if (entry.getValue().size() == 0) {
                writeInLine(String.format("\nAppropriate games for %s: no games", entry.getKey().toString()));
                continue;
            }
            writeInLine(String.format("\nAppropriate games for %s:", entry.getKey().toString()));
            int i = 0;
            for (Game game : entry.getValue()) {
                writeInLine(String.format("\n%d) %s", ++i, game));
            }
        }
        endMessage(LogType.BLOCK_END);
    }
}
