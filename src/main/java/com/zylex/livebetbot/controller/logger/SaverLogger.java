package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;

import java.util.List;
import java.util.Map;

public class SaverLogger extends ConsoleLogger {

    public void logRuleGames(Map<RuleNumber, List<Game>> ruleGames) {
        for (Map.Entry<RuleNumber, List<Game>> entry : ruleGames.entrySet()) {
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
