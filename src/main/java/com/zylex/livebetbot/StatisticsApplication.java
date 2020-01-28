package com.zylex.livebetbot;

import com.zylex.livebetbot.service.StatisticsCollector;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class StatisticsApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(StatisticsApplication.class);
        StatisticsCollector statisticsCollector = context.getBean(StatisticsCollector.class);
        statisticsCollector.analyse();
    }
}
