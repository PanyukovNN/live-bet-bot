package com.zylex.livebetbot;

import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.service.BetProcessor;
import com.zylex.livebetbot.service.CountryParser;
import com.zylex.livebetbot.service.ParseProcessor;

import java.io.IOException;
import java.util.List;

public class LiveBetBotApplication {

    public static void main(String[] args) {
        List<Game> games = new ParseProcessor(
            new DriverManager()
        ).process();
        System.out.println();
        games.forEach(System.out::println);
        /*
        1 шаг - найти все игры на перерыве, их счет и ссылки на них - выполнено
        2 шаг - отфильтровать игры по правилам
        3 шаг - сохранить в базу данных
        последний шаг - отсканировать все предыдущие игры, Не имеющие результата
        запустить бот через 10 минут.
        */
    }
}
