package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.service.StatisticsFileCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledStatisticsFileCreatingTask extends Thread {

    private StatisticsFileCreator statisticsFileCreator;

    @Autowired
    public ScheduledStatisticsFileCreatingTask(StatisticsFileCreator statisticsFileCreator) {
        this.statisticsFileCreator = statisticsFileCreator;
    }

    @Override
    public void run() {
        try {
            statisticsFileCreator.create();
        } catch (Throwable t) {
            t.printStackTrace();
            ConsoleLogger.writeErrorMessage(t.getMessage());
        }
    }
}
