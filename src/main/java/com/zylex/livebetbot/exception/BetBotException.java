package com.zylex.livebetbot.exception;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;

@SuppressWarnings("WeakerAccess")
public class BetBotException extends RuntimeException {

    public BetBotException(String message, Throwable cause) {
        super(message, cause);
        ConsoleLogger.writeExceptionToLog(message);
    }
}
