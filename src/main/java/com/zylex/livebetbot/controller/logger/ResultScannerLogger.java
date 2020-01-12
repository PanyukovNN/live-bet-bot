package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;

public class ResultScannerLogger extends ConsoleLogger {

    public synchronized void startLogMessage() {
        writeInLine("\nResult scanning: ...");
    }

    public void endLogMessage(LogType type, int gamesNumber) {
        String output = "";
        if (type == LogType.OKAY) {
            output = String.format("Result scanning: complete (found %d results)", gamesNumber);
            if (gamesNumber == 0) {
                output = "Result scanning: complete (no results found)";
            }
        } else if (type == LogType.NO_GAMES) {
            output = "Result scanning: complete (no games to scan)";
        } else if (type == LogType.ERROR) {
            output = "Result scanning: timeout error";
        }
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }
}
