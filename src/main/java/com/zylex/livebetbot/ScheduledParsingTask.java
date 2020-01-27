package com.zylex.livebetbot;

import com.zylex.livebetbot.service.Saver;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class ScheduledParsingTask implements Runnable {

    @Override
    public void run() {
        try {
            AnnotationConfigApplicationContext context =
                    new AnnotationConfigApplicationContext(ScheduledParsingTask.class);
            Saver saver = context.getBean(Saver.class);
            saver.save();
//            ResultScanner resultScanner = context.getBean(ResultScanner.class);
//            resultScanner.scan();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
