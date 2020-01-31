package com.zylex.livebetbot;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.ResultScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledResultScanningTask extends Thread {

    private DriverManager driverManager;

    private ResultScanner resultScanner;

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
            driverManager.initiateDriver(true);
        }
    }
}
