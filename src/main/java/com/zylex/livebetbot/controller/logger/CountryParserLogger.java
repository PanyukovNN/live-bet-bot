package com.zylex.livebetbot.controller.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CountryParserLogger extends ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(CountryParserLogger.class);

    private int totalCountries;

    private final AtomicInteger processedCountries = new AtomicInteger(0);

    private final AtomicInteger processedErrorCountries = new AtomicInteger(0);

    private int currentLength;

    public void startLogMessage(int countriesCount) {
        totalCountries = countriesCount;
        processedCountries.set(0);
        processedErrorCountries.set(0);
        String output = String.format("\nProcessing countries: 0/%d (0.0%%)", countriesCount);
        currentLength = output.length() - 23;
        writeInLine(output);
        LOG.info("Processing countries");
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
                writeInLine("\n" + output);
                LOG.warn(output);
            }
            writeLineSeparator();
            LOG.info(String.format("%d countries processed", processedCountries.get()));
        }
    }
}
