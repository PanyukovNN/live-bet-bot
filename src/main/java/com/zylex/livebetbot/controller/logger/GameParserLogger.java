package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameParserLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(GameParserLogger.class);

    private int totalGames;

    private AtomicInteger processedGames = new AtomicInteger();

    private AtomicInteger processedErrorGames = new AtomicInteger();

    private int currentLength;

    public void startLogMessage(LogType type, int arg) {
        totalGames = 0;
        processedGames.set(0);
        processedErrorGames.set(0);
        if (type == LogType.OKAY) {
            totalGames = arg;
            String output = String.format("\nProcessing games: 0/%d (0.0%%)", arg);
            currentLength = output.length() - 19;
            writeInLine(output);
            LOG.info("Processing games");
        } else if (type == LogType.NO_GAMES) {
            writeInLine("\nProcessing games: no HT games");
            writeLineSeparator();
            LOG.info("No HT games");
        }
    }

    public synchronized void logGame(LogType type) {
        if (type == LogType.OKAY) {
            String output = String.format("%d/%d (%s%%)",
                    processedGames.incrementAndGet(),
                    totalGames,
                    new DecimalFormat("#0.0").format(((double) processedGames.get() / (double) totalGames) * 100).replace(",", "."));
            writeInLine(StringUtils.repeat("\b", currentLength) + output);
            currentLength = output.length();
        } else if (type == LogType.ERROR) {
            processedErrorGames.incrementAndGet();
        }
        if (processedGames.get() + processedErrorGames.get() == totalGames) {
            if (processedErrorGames.get() > 0) {
                String output = String.format("Not processed games: %d", processedErrorGames.get());
                writeErrorMessage("\n" + output);
                LOG.warn(output);
            }
            LOG.info(String.format("%d games processed", processedGames.get()));
            writeLineSeparator();
        }
    }
}
