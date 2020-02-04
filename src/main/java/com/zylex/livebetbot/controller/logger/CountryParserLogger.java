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

    private int currentLength;

    public void startLogMessage(int countriesCount) {
        totalCountries = countriesCount;
        processedCountries = new AtomicInteger();
        processedErrorCountries = new AtomicInteger();
        String output = String.format("\nProcessing countries: 0/%d (0.0%%)", countriesCount);
        currentLength = output.length() - 23;
        writeInLine(output);
        LOG.info("Processing countries");
    }

    public void logCountriesFound(LogType type) {
        if (type == LogType.OKAY) {
            writeInLine("\b\b\bcomplete");
            LOG.info("Countries found");
        } else if (type == LogType.NO_COUNTRIES) {
            writeInLine("\b\b\bcomplete (no countries)");
            LOG.info("No countries found");
        }
        writeLineSeparator();
    }

    public synchronized void logCountry(LogType type) {
        if (type == LogType.ERROR) {
            processedErrorCountries.incrementAndGet();
        } else if (type == LogType.OKAY) {
            String output = String.format("%d/%d (%s%%)",
                    processedCountries.incrementAndGet(),
                    totalCountries,
                    new DecimalFormat("#0.0").format(((double) processedCountries.get() / (double) totalCountries) * 100).replace(",", "."));
            writeInLine(StringUtils.repeat("\b", currentLength) + output);
            currentLength = output.length();
        }
        if (processedCountries.get() + processedErrorCountries.get() == totalCountries) {
            if (processedErrorCountries.get() > 0) {
                String output = String.format("Countries with no handicap matches: %d", processedErrorCountries.get());
                writeErrorMessage("\n" + output);
                LOG.warn(output);
            }
            writeLineSeparator();
            LOG.info(String.format("%d countries processed", processedCountries.get()));
        }
    }
}
