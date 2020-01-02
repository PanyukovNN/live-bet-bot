package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class ParserLogger extends ConsoleLogger {

    private int totalCountries;

    private int totalGames;

    private AtomicInteger processedCountries = new AtomicInteger();

    private AtomicInteger processedGames = new AtomicInteger();

    public synchronized void startLogMessage(LogType type, Integer arg) {
        if (type == LogType.PARSING_START) {
            writeInLine("\nParsing started.");
            writeInLine("\nFinding countries: ...");
        } else if (type == LogType.COUNTRIES) {
            totalCountries = arg;
            writeInLine(String.format("\nProcessing countries: 0/%d (0.0%%)", arg));
        } else if (type == LogType.GAMES) {
            totalGames = arg;
            writeInLine(String.format("\nProcessing games: 0/%d (0.0%%)", arg));
            if (totalGames == 0) {
                parsingComlete();
            }
        }
    }

    public void logCountriesFound() {
        String output = "Finding countries: complete";
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }

    public synchronized void logCountry() {
        String output = String.format("Processing countries: %d/%d (%s%%)",
                processedCountries.incrementAndGet(),
                totalCountries,
                new DecimalFormat("#0.0").format(((double) processedCountries.get() / (double) totalCountries) * 100).replace(",", "."));
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        if (processedCountries.get() == totalCountries) {
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
            parsingComlete();
            writeLineSeparator();
        }
    }

    private void parsingComlete() {
        writeInLine(String.format("\nParsing completed in %s", computeTime(programStartTime.get())));
    }
}
