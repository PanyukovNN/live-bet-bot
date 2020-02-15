package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.StatisticsFileCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StatisticsFileCreatorLogger extends ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(StatisticsFileCreator.class);

    public void log(LogType type) {
        String output = "";
        if (type == LogType.OKAY) {
            output = "Statistics file(-s) creation: completed";
        } else if (type == LogType.ERROR) {
            output = "Statistics file(-s) creation: error";
        }
        writeInLine("\n" + output);
        writeLineSeparator();
        LOG.info(output);
    }
}
