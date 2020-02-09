package com.zylex.livebetbot.controller.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.rule.RuleNumber;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SaverLogger extends ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(Saver.class);

    public void logRuleGames(Map<RuleNumber, List<Game>> ruleGames) {
        for (Map.Entry<RuleNumber, List<Game>> entry : ruleGames.entrySet()) {
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
