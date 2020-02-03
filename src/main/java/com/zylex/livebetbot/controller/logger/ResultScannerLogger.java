package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.ResultScanner;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ResultScannerLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(ResultScanner.class);

    public synchronized void startLogMessage() {
        writeInLine("\nResult scanning started");
        writeLineSeparator();
        LOG.info("Result scanning started");
    }

    public void endLogMessage(LogType type, int gamesNumber) {
        String output = "";
        if (type == LogType.OKAY) {
            output = gamesNumber > 0
                    ? String.format("Scanning complete: found %d results", gamesNumber)
                    : "Scanning complete: no results found";
        } else if (type == LogType.NO_GAMES) {
            output = "Scanning complete: no games to scan";
        } else if (type == LogType.ERROR) {
            output = "Error while scanning";
        }
        writeInLine("\n" + output);
        LOG.info(output);
    }

    public void logIn(LogType type) {
        String output = "";
        if (type == LogType.OKAY) {
            output = "Logged in successful";
        } else if (type == LogType.ERROR) {
            output = "Error while logging in";
        }
        writeInLine("\n" + output);
        writeLineSeparator();
        LOG.info(output);
    }

    public void logOut(LogType type) {
        String output = "";
        if (type == LogType.OKAY) {
            output = "Logged out successful";
        } else if (type == LogType.ERROR) {
            output = "Error while logging out";
        }
        writeInLine("\n" + output);
        LOG.info(output);
    }

    public void fileCreatedSuccessfully(LogType type, LocalDate date) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String output = "";
        if (type == LogType.OKAY) {
            output = String.format("Statistics file (%s): created successfully", DATE_FORMATTER.format(date));
        } else if (type == LogType.NO_GAMES) {
            output = String.format("Statistics file (%s): no games to save", DATE_FORMATTER.format(date));
        }
        writeInLine("\n" + output);
        writeLineSeparator();
        LOG.info(output);
    }

    public void noResultGamesFound(int noResultGamesCount) {
        String output = String.format("Found %d games with no result", noResultGamesCount);
        writeInLine("\n" + output);
        writeLineSeparator();
        LOG.info(output);
    }
}
