package com.zylex.livebetbot;

import com.zylex.livebetbot.service.HibernateUtil;
import com.zylex.livebetbot.service.StatisticsCollector;
import com.zylex.livebetbot.service.repository.GameRepository;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class StatisticsApplication {

    public static void main(String[] args) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime startDateTime = args.length > 0
                ? LocalDateTime.of(LocalDate.parse(args[0], DATE_FORMATTER), LocalTime.MIN)
                : LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDateTime = args.length > 1
                ? LocalDateTime.of(LocalDate.parse(args[1], DATE_FORMATTER), LocalTime.MAX)
                : LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.MAX);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            new StatisticsCollector(
                new GameRepository(session),
                startDateTime,
                endDateTime
            ).analyse();
        }
    }
}
