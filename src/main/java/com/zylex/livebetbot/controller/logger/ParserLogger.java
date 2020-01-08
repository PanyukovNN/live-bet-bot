package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ParserLogger extends ConsoleLogger {

    private AtomicLong parsingStartTime = new AtomicLong(System.currentTimeMillis());

    private int totalCountries;

    private int totalGames;

    private AtomicInteger processedCountries = new AtomicInteger();

    private AtomicInteger processedGames = new AtomicInteger();

    private AtomicInteger processedErrorCountries = new AtomicInteger();

    public synchronized void startLogMessage(LogType type, Integer arg) {
        if (type == LogType.PARSING_START) {
            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a dd.MM.yyyy");
            writeInLine("\nParsing started at " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
            writeInLine("\nFinding countries: ...");
        } else if (type == LogType.COUNTRIES) {
            totalCountries = arg;
            writeInLine(String.format("\nProcessing countries: 0/%d (0.0%%)", arg));
        } else if (type == LogType.GAMES) {
            totalGames = arg;
            writeInLine(String.format("\nProcessing games: 0/%d (0.0%%)", arg));
        } else if (type == LogType.NO_GAMES) {
            writeInLine("\nProcessing games: no games");
        }
    }

    public void logCountriesFound(LogType type) {
        if (type == LogType.OKAY) {
            String output = "Finding countries: complete";
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
            writeLineSeparator();
        } else if (type == LogType.NO_COUNTRIES) {
            String output = "Finding countries: complete (no countries)";
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
        }
    }

    public synchronized void logCountry(LogType type) {
        if (type == LogType.ERROR) {
            processedErrorCountries.incrementAndGet();
            return;
        } else if (type == LogType.OKAY) {
            String output = String.format("Processing countries: %d/%d (%s%%)",
                    processedCountries.incrementAndGet(),
                    totalCountries,
                    new DecimalFormat("#0.0").format(((double) processedCountries.get() / (double) totalCountries) * 100).replace(",", "."));
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
        }
        if (processedCountries.get() == totalCountries) {
            if (processedErrorCountries.get() > 0) {
                String output = String.format("Countries with no handicap matches: %d", processedErrorCountries.get());
                writeErrorMessage(output);
            }
            writeLineSeparator();
        }
    }

    public synchronized void logGame() {
        String output = String.format("Processing games: %d/%d (%s%%)",
                processedGames.incrementAndGet(),
                totalGames,
                new DecimalFormat("#0.0").format(((double) processedGames.get() / (double) totalGames) * 100).replace(",", "."));
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        if (processedGames.get() == totalGames) {
            writeLineSeparator();
            parsingComplete();
        }
    }

    private void parsingComplete() {
        writeInLine(String.format("\nParsing completed in %s", computeTime(parsingStartTime.get())));
        writeLineSeparator();
    }
}
