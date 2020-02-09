package com.zylex.livebetbot.service.rule;

import com.zylex.livebetbot.model.GameTime;

public enum RuleNumber {
    FIRST(new FirstRule(), "0:0", new GameTime(GameTime.Half.HALF_TIME, 0, 0)),
    SECOND(new SecondRule(), "0:0", new GameTime(GameTime.Half.HALF_TIME, 0, 0));

    public Rule rule;

    public String score;

    public GameTime gameTime;

    RuleNumber(Rule rule, String score, GameTime gameTime) {
        this.rule = rule;
        this.score = score;
        this.gameTime = gameTime;
    }
}
