package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.parser.ParseProcessor;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class ParseProcessorLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(ParseProcessor.class);

    private AtomicLong parsingStartTime = new AtomicLong(System.currentTimeMillis());

    public void startLogMessage() {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a dd.MM.yyyy");
        writeInLine("\nParsing started at " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        writeInLine("\nFinding countries: ...");
        LOG.info("Parsing started");
        LOG.info("Finding countries");
    }

    public void parsingComplete() {
        writeInLine(String.format("\nParsing completed in %s", computeTime(parsingStartTime.get())));
        writeLineSeparator();
        LOG.info("Parsing completed");
    }
}
