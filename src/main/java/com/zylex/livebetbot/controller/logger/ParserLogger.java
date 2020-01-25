package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.parser.ParseProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ParserLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(ParseProcessor.class);

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
            LOG.info("Parsing started");
            LOG.info("Finding countries");
        } else if (type == LogType.COUNTRIES) {
            totalCountries = arg;
            writeInLine(String.format("\nProcessing countries: 0/%d (0.0%%)", arg));
            LOG.info("Processing countries");
        } else if (type == LogType.GAMES) {
            totalGames = arg;
            writeInLine(String.format("\nProcessing games: 0/%d (0.0%%)", arg));
            LOG.info("Processing games");
        } else if (type == LogType.NO_GAMES) {
            writeInLine("\nProcessing games: no HT games");
            LOG.info("No HT games");
        }
    }

    public void logCountriesFound(LogType type) {
        if (type == LogType.OKAY) {
            String output = "Finding countries: complete";
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
            writeLineSeparator();
            LOG.info("Countries found");
        } else if (type == LogType.NO_COUNTRIES) {
            String output = "Finding countries: complete (no countries)";
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
            LOG.info("No countries found");
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
                LOG.warn(output);
            }
            writeLineSeparator();
            LOG.info(String.format("%d countries processed", processedCountries.get()));
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
            parsingComplete();
        }
    }

    private void parsingComplete() {
        writeInLine(String.format("\nParsing completed in %s", computeTime(parsingStartTime.get())));
        writeLineSeparator();
        LOG.info("Parsing completed");
    }
}
