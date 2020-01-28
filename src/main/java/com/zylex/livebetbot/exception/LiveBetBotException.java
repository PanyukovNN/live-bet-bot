package com.zylex.livebetbot.exception;

import org.apache.log4j.Logger;

public class LiveBetBotException extends RuntimeException {

    private static Logger LOG = Logger.getLogger(LiveBetBotException.class);

    public LiveBetBotException(String message, Throwable cause) {
        super(message, cause);
        LOG.error(message, cause);
        System.out.println();
        cause.printStackTrace();
    }
}
