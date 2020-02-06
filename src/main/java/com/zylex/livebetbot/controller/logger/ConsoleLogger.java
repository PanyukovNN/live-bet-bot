package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.LiveBetBotApplication;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base class for loggers.
 */
public abstract class ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(LiveBetBotApplication.class);

    private static AtomicLong programStartTime = new AtomicLong(System.currentTimeMillis());

    static {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a dd.MM.yyyy");
        String output = StringUtils.repeat("*", 50) + "\n"
                + String.format("Bot started at: %s", LocalDateTime.now().format(DATE_TIME_FORMATTER))
                + " (ver.0.01)";
        System.out.print(output);
        writeLineSeparator();
        LOG.info("Bot started");
    }

    public synchronized static void endMessage(LogType type) {
        if (type == LogType.BOT_END) {
            String output = "\nBot work completed in " + computeTime(programStartTime.get())
            + "\n" + StringUtils.repeat("*", 50) + "\n";
            writeInLine(output);
            LOG.info("Bot work completed");
        } else if (type == LogType.BLOCK_END) {
            String output = "\n" + StringUtils.repeat("~", 50);
            writeInLine(output);
            LOG.info("Block completed");
        }
    }

    static synchronized void writeLineSeparator() {
        String line = "\n" + StringUtils.repeat("-", 50);
        writeInLine(line);
    }

    public static synchronized void writeErrorMessage(String message) {
        System.err.print(message);
        LOG.error(message);
    }

    static synchronized void writeInLine(String line) {
        System.out.print(line);
    }

    static String computeTime(long startTime) {
        long millis = System.currentTimeMillis() - startTime;
        String time = String.format("%02d min. %02d sec.",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        if (hours > 0) {
            return String.format("%02d h. ", hours) + time;
        } else {
            return time;
        }
    }
}
