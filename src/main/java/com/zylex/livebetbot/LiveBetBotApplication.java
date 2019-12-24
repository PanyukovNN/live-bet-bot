package com.zylex.livebetbot;

import com.zylex.livebetbot.service.BetProcessor;

import java.io.IOException;

public class LiveBetBotApplication {

    public static void main(String[] args) throws InterruptedException, IOException {
        BetProcessor betProcessor = new BetProcessor();
        betProcessor.process();
    }
}
