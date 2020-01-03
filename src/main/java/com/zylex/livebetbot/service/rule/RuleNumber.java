package com.zylex.livebetbot.service.rule;

public enum RuleNumber {
    FIRST_RULE(new FirstRule()),
    SECOND_RULE(new SecondRule());

    Rule rule;

    RuleNumber(Rule rule) {
        this.rule = rule;
    }
}
