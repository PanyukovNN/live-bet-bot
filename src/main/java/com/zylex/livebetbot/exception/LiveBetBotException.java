package com.zylex.livebetbot.exception;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;

public class LiveBetBotException extends RuntimeException {

    public LiveBetBotException(String message, Throwable cause) {
        super(message, cause);
        ConsoleLogger.writeExceptionToLog(message);
    }
}
