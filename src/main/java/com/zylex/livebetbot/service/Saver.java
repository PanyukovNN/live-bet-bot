package com.zylex.livebetbot.service;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.controller.logger.SaverLogger;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;
import org.hibernate.Session;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class Saver {

    private static final Logger LOG = Logger.getLogger(Saver.class.getName());

    private SaverLogger logger = new SaverLogger();

    private RuleProcessor ruleProcessor;

    private Session session;

    public Saver(RuleProcessor ruleProcessor, Session session) {
        this.ruleProcessor = ruleProcessor;
        this.session = session;
    }

    public void save() {
        Map<RuleNumber, Set<Game>> ruleGames = ruleProcessor.process();
        if (ruleGames.isEmpty()) {
            ConsoleLogger.endMessage(LogType.BLOCK_END);
            return;
        }
        logger.logRuleGames(ruleGames);
        session.beginTransaction();
//        ruleGames.forEach((k, v) -> v.forEach(session::save));
        for (RuleNumber ruleNumber : ruleGames.keySet()) {
            for (Game game : ruleGames.get(ruleNumber)) {
                System.out.println(game);
                game.getOverUnderSet().forEach(System.out::println);
                session.save(game);
                session.flush();
            }
        }
        session.getTransaction().commit();
    }
}
