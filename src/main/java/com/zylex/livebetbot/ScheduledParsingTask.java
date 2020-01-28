package com.zylex.livebetbot;

import com.zylex.livebetbot.service.ResultScanner;
import com.zylex.livebetbot.service.Saver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledParsingTask implements Runnable {

    private Saver saver;

    private ResultScanner resultScanner;

    @Autowired
    public ScheduledParsingTask(Saver saver, ResultScanner resultScanner) {
        this.saver = saver;
        this.resultScanner = resultScanner;
    }

    @Override
    public void run() {
        try {
            saver.save();
//            resultScanner.scan();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
