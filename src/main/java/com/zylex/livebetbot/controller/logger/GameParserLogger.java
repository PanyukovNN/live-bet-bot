package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class GameParserLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(GameParserLogger.class);

    private int totalGames;

    private AtomicInteger processedGames;

    public void startLogMessage(LogType type, int arg) {
        totalGames = 0;
        processedGames = new AtomicInteger();
        if (type == LogType.OKAY) {
            totalGames = arg;
            writeInLine(String.format("\nProcessing games: 0/%d (0.0%%)", arg));
            LOG.info("Processing games");
        } else if (type == LogType.NO_GAMES) {
            writeInLine("\nProcessing games: no HT games");
            writeLineSeparator();
            LOG.info("No HT games");
        }
    }

    public synchronized void logGame() {
        String output = String.format("Processing games: %d/%d (%s%%)",
                processedGames.incrementAndGet(),
                totalGames,
                new DecimalFormat("#0.0").format(((double) processedGames.get() / (double) totalGames) * 100).replace(",", "."));
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        if (processedGames.get() == totalGames) {
            LOG.info(String.format("%d games processed", processedGames.get()));
            writeLineSeparator();
        }
    }
}
