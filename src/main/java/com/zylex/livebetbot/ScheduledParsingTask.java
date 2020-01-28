package com.zylex.livebetbot;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.Saver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledParsingTask implements Runnable {

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
            driverManager.refreshDriver();
            saver.save();
        } catch (Throwable t) {
            t.printStackTrace();
            driverManager.refreshDriver();
        }
    }
}
