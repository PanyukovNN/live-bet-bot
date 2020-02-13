package com.zylex.livebetbot;

import com.zylex.livebetbot.controller.logger.ConsoleLogger;
import com.zylex.livebetbot.service.ResultScanner;
import com.zylex.livebetbot.service.driver.DriverManager;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScheduledResultScanningTask extends Thread {

    private ResultScanner resultScanner;

    private DriverManager driverManager;

    private GameRepository gameRepository;

    private SessionFactory sessionFactory;

    @Autowired
    public ScheduledResultScanningTask(DriverManager driverManager, ResultScanner resultScanner, GameRepository gameRepository, SessionFactory sessionFactory) {
        this.driverManager = driverManager;
        this.resultScanner = resultScanner;
        this.gameRepository = gameRepository;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        try {
            LocalDate date = LocalDate.of(2020, 2, 1);
            while (date.compareTo(LocalDate.now()) <= 0) {
                gameRepository.createStatisticsFile(date);
                date = date.plusDays(1);
            }
            System.out.print("\nFiles created");
//            resultScanner.scan();
        } catch (Throwable t) {
            t.printStackTrace();
            ConsoleLogger.writeErrorMessage(t.getMessage());
//            driverManager.refreshDriver();
        }
    }
}
