package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.filecreator.LeagueFileCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LeagueFileCreatorLogger extends ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(LeagueFileCreator.class);

    public void log(LogType type) {
        String output = "";
        if (type == LogType.OKAY) {
            output = "League file creation: completed";
        } else if (type == LogType.ERROR) {
            output = "League file creation: error";
        } else if (type == LogType.NO_LEAGUES) {
            output = "League file creation: no new leagues";
        }
        writeInLine("\n" + output);
        LOG.info(output);
    }
}
