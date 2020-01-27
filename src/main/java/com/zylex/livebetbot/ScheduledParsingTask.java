package com.zylex.livebetbot;

import com.zylex.livebetbot.service.DriverManager;
import com.zylex.livebetbot.service.Saver;
import com.zylex.livebetbot.service.repository.HibernateUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

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
            HibernateUtil.getSessionFactory().getCurrentSession().close();
            DriverManager driverManager = context.getBean(DriverManager.class);
            driverManager.quitDriver();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
