package com.zylex.livebetbot.exception;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

public class LiveBetBotException extends RuntimeException {

    private static Logger LOG = LoggerFactory.getLogger(LiveBetBotException.class);

    public LiveBetBotException(String message, Throwable cause) {
        super(message, cause);
        LOG.error(message, cause);
        System.out.println();
        cause.printStackTrace();
    }
}
