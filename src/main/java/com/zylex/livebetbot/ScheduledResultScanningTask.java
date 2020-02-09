package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.service.driver.DriverManager;
import com.zylex.livebetbot.service.ResultScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledResultScanningTask extends Thread {

    private ResultScanner resultScanner;

    private DriverManager driverManager;

    @Autowired
    public ScheduledResultScanningTask(DriverManager driverManager, ResultScanner resultScanner) {
        this.driverManager = driverManager;
        this.resultScanner = resultScanner;
    }

    @Override
    public void run() {
        try {
            resultScanner.scan();
        } catch (Throwable t) {
            t.printStackTrace();
            ConsoleLogger.writeErrorMessage(t.getMessage());
            driverManager.refreshDriver();
        }
    }
}
