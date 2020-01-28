package com.zylex.livebetbot;

import com.zylex.livebetbot.service.Saver;

public class ScheduledParsingTask implements Runnable {

    private Saver saver;

    public ScheduledParsingTask(Saver saver) {
        this.saver = saver;
    }

    @Override
    public void run() {
        try {
            saver.save();
//            ResultScanner resultScanner = context.getBean(ResultScanner.class);
//            resultScanner.scan();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
