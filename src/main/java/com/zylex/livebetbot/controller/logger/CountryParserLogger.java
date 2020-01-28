package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class CountryParserLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(CountryParserLogger.class);

    private int totalCountries;

    private AtomicInteger processedCountries;

    private AtomicInteger processedErrorCountries;

    public void startLogMessage(int countriesCount) {
        totalCountries = countriesCount;
        processedCountries = new AtomicInteger();
        processedErrorCountries = new AtomicInteger();
        writeInLine(String.format("\nProcessing countries: 0/%d (0.0%%)", countriesCount));
        LOG.info("Processing countries");
    }

    public void logCountriesFound(LogType type) {
        if (type == LogType.OKAY) {
            String output = "Finding countries: complete";
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
            LOG.info("Countries found");
        } else if (type == LogType.NO_COUNTRIES) {
            String output = "Finding countries: complete (no countries)";
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
            LOG.info("No countries found");
        }
        writeLineSeparator();
    }

    public synchronized void logCountry(LogType type) {
        if (type == LogType.ERROR) {
            processedErrorCountries.incrementAndGet();
        } else if (type == LogType.OKAY) {
            String output = String.format("Processing countries: %d/%d (%s%%)",
                    processedCountries.incrementAndGet(),
                    totalCountries,
                    new DecimalFormat("#0.0").format(((double) processedCountries.get() / (double) totalCountries) * 100).replace(",", "."));
            writeInLine(StringUtils.repeat("\b", output.length()) + output);
        }
        if (processedCountries.get() + processedErrorCountries.get() == totalCountries) {
            if (processedErrorCountries.get() > 0) {
                String output = String.format("Countries with no handicap matches: %d", processedErrorCountries.get());
                writeErrorMessage(output);
                LOG.warn(output);
            }
            writeLineSeparator();
            LOG.info(String.format("%d countries processed", processedCountries.get()));
        }
    }
}
