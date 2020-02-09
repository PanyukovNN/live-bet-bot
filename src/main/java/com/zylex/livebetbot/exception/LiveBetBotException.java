package com.zylex.livebetbot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveBetBotException extends RuntimeException {

    private static Logger LOG = LoggerFactory.getLogger(LiveBetBotException.class);

    public LiveBetBotException(String message, Throwable cause) {
        super(message, cause);
        LOG.error(message, cause);
        System.out.println();
        cause.printStackTrace();
    }
}
