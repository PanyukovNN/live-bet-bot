package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.driver.DriverManager;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ScheduledParsingTask extends Thread {

    private DriverManager driverManager;

    private ParseProcessor parseProcessor;

    private RuleProcessor ruleProcessor;

    private Saver saver;

    @Autowired
    public ScheduledParsingTask(DriverManager driverManager,
                                ParseProcessor parseProcessor,
                                RuleProcessor ruleProcessor,
                                Saver saver) {
        this.driverManager = driverManager;
        this.parseProcessor = parseProcessor;
        this.ruleProcessor = ruleProcessor;
        this.saver = saver;
    }

    @Override
    public void run() {
        try {
            Set<Game> appropriateGames = parseProcessor.process();
            Map<RuleNumber, List<Game>> ruleGames = ruleProcessor.process(appropriateGames);
            saver.save(ruleGames);
        } catch (Throwable t) {
            t.printStackTrace();
            ConsoleLogger.writeErrorMessage(t.getMessage());
            driverManager.refreshDriver();
        }
    }
}
