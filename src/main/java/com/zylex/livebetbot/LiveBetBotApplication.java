package com.zylex.livebetbot;

import com.zylex.livebetbot.service.BetProcessor;
import com.zylex.livebetbot.service.CountryParser;
import com.zylex.livebetbot.service.ParseProcessor;

import java.io.IOException;

public class LiveBetBotApplication {

    public static void main(String[] args) throws InterruptedException, IOException {
//        BetProcessor betProcessor = new BetProcessor();
//        betProcessor.process();
        new ParseProcessor(
            new DriverManager()
        ).process();
        /*
        1 шаг - найти все игры на перерыве, их счет и ссылки на них
        2 шаг - отфильтровать игры по правилам
        3 шаг - сохранить в базу данных
        последний шаг - отсканировать все предыдущие игры, Не имеющие результата
        запустить бот через 10 минут.
        */
    }
}
