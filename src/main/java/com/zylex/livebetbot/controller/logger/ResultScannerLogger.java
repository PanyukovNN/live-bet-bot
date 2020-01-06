package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;

public class ResultScannerLogger extends ConsoleLogger {

    public synchronized void startLogMessage() {
        writeInLine("\nResult scanning: ...");
    }

    public void endLogMessage(int gamesNumber) {
        String output = "Result scanning: complete";
        output += gamesNumber == 0
                ? " (no results found)"
                : String.format(" (found %d results)", gamesNumber);
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }
}
