package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.DriverManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Logs DriverManager.
 */
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
        String output = "Starting web driver: complete";
        LOG.info("Driver started");
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        writeLineSeparator();
    }
}
