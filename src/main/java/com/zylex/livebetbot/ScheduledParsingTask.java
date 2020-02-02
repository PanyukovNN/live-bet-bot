package com.zylex.livebetbot;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ScheduledParsingTask extends Thread {

    private ParseProcessor parseProcessor;

    private RuleProcessor ruleProcessor;

    private Saver saver;

    @Autowired
    public ScheduledParsingTask(ParseProcessor parseProcessor,
                                RuleProcessor ruleProcessor,
                                Saver saver) {
        this.parseProcessor = parseProcessor;
        this.ruleProcessor = ruleProcessor;
        this.saver = saver;
    }

    @Override
    public void run() {
        try {
            List<Game> appropriateGames = parseProcessor.process();
            Map<RuleNumber, List<Game>> ruleGames = ruleProcessor.process(appropriateGames);
            saver.save(ruleGames);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
