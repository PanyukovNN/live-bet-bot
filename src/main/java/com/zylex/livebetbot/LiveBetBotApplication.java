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
            new DriverManager(),
            new CountryParser()
        ).process();
    }
}
