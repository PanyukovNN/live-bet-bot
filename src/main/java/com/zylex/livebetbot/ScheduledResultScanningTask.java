package com.zylex.livebetbot;

import com.zylex.livebetbot.service.ResultScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledResultScanningTask extends Thread {

    private ResultScanner resultScanner;

    @Autowired
    public ScheduledResultScanningTask(ResultScanner resultScanner) {
        this.resultScanner = resultScanner;
    }

    @Override
    public void run() {
        try {
            resultScanner.scan();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
