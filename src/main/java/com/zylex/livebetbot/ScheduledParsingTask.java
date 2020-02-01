package com.zylex.livebetbot;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.Saver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledParsingTask extends Thread {

    private Saver saver;

    private DriverManager driverManager;

    @Autowired
    public ScheduledParsingTask(DriverManager driverManager, Saver saver) {
        this.driverManager = driverManager;
        this.saver = saver;
    }

    @Override
    public void run() {
        try {
            saver.save();
        } catch (Throwable t) {
            t.printStackTrace();
            driverManager.initiateDriver(true);
        }
    }
}
