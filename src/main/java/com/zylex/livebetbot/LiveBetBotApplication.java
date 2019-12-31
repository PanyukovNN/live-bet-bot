package com.zylex.livebetbot;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.BetProcessor;
import com.zylex.livebetbot.service.CountryParser;
import com.zylex.livebetbot.service.ParseProcessor;
import com.zylex.livebetbot.service.rule.RuleNumber;
import com.zylex.livebetbot.service.rule.RuleProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LiveBetBotApplication {

    public static void main(String[] args) {
        Map<RuleNumber, List<Game>> ruleGames =
                new RuleProcessor(
                    new ParseProcessor(
                    new DriverManager()
                )).process();
        System.out.println();
        ruleGames.forEach((k, v) -> v.forEach(System.out::println));
        /*
        1 шаг - найти все игры на перерыве, их счет и ссылки на них - выполнено
        2 шаг - отфильтровать игры по правилам - выполнено
        3 шаг - сохранить в базу данных
        последний шаг - отсканировать все предыдущие игры, Не имеющие результата
        запустить бот через 10 минут.
        */
    }
}
