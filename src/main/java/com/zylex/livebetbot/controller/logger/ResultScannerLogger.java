package com.zylex.livebetbot.controller.logger;

import org.apache.commons.lang3.StringUtils;

public class ResultScannerLogger extends ConsoleLogger {

    public synchronized void startLogMessage() {
        writeInLine("\nResult scanning: ...");
    }

    public void endLogMessage() {
        String output = "Result scanning: complete";
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
    }
}
