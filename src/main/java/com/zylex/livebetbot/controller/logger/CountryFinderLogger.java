package com.zylex.livebetbot.controller.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CountryFinderLogger extends ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(CountryFinderLogger.class);

    public void startLogMessage() {
        writeInLine("\nFinding countries: ...");
        LOG.info("Finding countries");
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
}
