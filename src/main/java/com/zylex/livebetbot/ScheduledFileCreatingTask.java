package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.controller.logger.LogType;
import com.zylex.livebetbot.service.filecreator.LeagueFileCreator;
import com.zylex.livebetbot.service.filecreator.StatisticsFileCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledFileCreatingTask extends Thread {

    private StatisticsFileCreator statisticsFileCreator;

    private LeagueFileCreator leagueFileCreator;

    @Autowired
    public ScheduledFileCreatingTask(StatisticsFileCreator statisticsFileCreator, LeagueFileCreator ligueFileCreator) {
        this.statisticsFileCreator = statisticsFileCreator;
        this.leagueFileCreator = ligueFileCreator;
    }

    @Override
    public void run() {
        try {
            statisticsFileCreator.create();
            leagueFileCreator.create();
            ConsoleLogger.endMessage(LogType.BLOCK_END);
        } catch (Throwable t) {
            t.printStackTrace();
            ConsoleLogger.writeErrorMessage(t.getMessage());
        }
    }
}
