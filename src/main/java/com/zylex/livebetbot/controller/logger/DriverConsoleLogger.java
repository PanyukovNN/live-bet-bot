package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.DriverManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Logs DriverManager.
 */
@Service
public class DriverConsoleLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(DriverManager.class);

    /**
     * Log start message.
     */
    public void startLogMessage() {
        writeInLine("\nStarting web driver: ...");
        LOG.info("Starting web driver");
    }

    /**
     * Log driver start.
     */
    public void logDriver() {
        LOG.info("Driver started");
        writeInLine("\b\b\bcomplete");
        writeLineSeparator();
    }
}
