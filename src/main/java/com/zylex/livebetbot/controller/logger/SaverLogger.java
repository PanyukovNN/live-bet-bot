package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;

public class SaverLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(Saver.class);

    public void logRuleGames(Map<RuleNumber, Set<Game>> ruleGames) {
        for (Map.Entry<RuleNumber, Set<Game>> entry : ruleGames.entrySet()) {
            if (entry.getValue().size() == 0) {
                writeInLine(String.format("\nAppropriate games for %s: no games", entry.getKey().toString()));
                LOG.info(String.format("Appropriate games for %s: no games", entry.getKey().toString()));
                continue;
            }
            writeInLine(String.format("\nAppropriate games for %s:", entry.getKey().toString()));
            LOG.info(String.format("Appropriate games for %s:", entry.getKey().toString()));
            int i = 0;
            for (Game game : entry.getValue()) {
                writeInLine(String.format("\n%d) %s", ++i, game));
                LOG.info(String.format("%d) %s", i, game));
            }
        }
        endMessage(LogType.BLOCK_END);
    }
}
