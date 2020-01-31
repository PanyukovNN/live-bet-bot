package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.exception.GameRepositoryException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.OverUnder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.Query;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class GameRepository {

    private Session session;

    @PostConstruct
    private void postConstruct() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            SessionFactory sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
            session = sessionFactory.openSession();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @PreDestroy
    private void preDestroy() {
        session.close();
    }

    public List<Game> getWithoutResult() {
        Query query = session.createQuery("FROM Game WHERE finalScore IS NULL OR finalScore = '-1:-1'");
        return query.getResultList();
    }

    public List<Game> getByDate(LocalDate date) {
        LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
        Query query = session.createQuery("FROM Game WHERE dateTime >= :dayStart AND dateTime <= :dayEnd");
        query.setParameter("dayStart", dayStart);
        query.setParameter("dayEnd", dayEnd);
        return query.getResultList();
    }

    public void save(Game game) {
        session.beginTransaction();
        session.save(game);
        session.getTransaction().commit();
    }

    public void save(List<Game> games) {
        session.beginTransaction();
        games.forEach(session::save);
        session.getTransaction().commit();
    }

    public boolean createStatisticsFile(LocalDate date) {
        List<Game> games = getByDate(date);
        if (games.isEmpty()) {
            return false;
        }
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String fileName = String.format("statistics/%s.csv", DATE_FORMATTER.format(date));
        if (!createFile(fileName)) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), StandardCharsets.UTF_8))) {
            String GAME_BODY_FORMAT = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n";
            for (Game game : games) {
                String over10 = findOverUnder(game.getOverUnderList(), OverUnder.Type.OVER, 1);
                String over15 = findOverUnder(game.getOverUnderList(), OverUnder.Type.OVER, 1.5);
                String under10 = findOverUnder(game.getOverUnderList(), OverUnder.Type.UNDER, 1);
                String under15 = findOverUnder(game.getOverUnderList(), OverUnder.Type.UNDER, 1.5);
                String output = String.format(GAME_BODY_FORMAT,
                        DATE_FORMATTER.format(game.getDateTime()),
                        game.getFirstTeam(), game.getSecondTeam(),
                        game.getHalfTimeScore(), game.getFinalScore(),
                        game.getRuleNumber(), over10, over15, under10, under15);
                writer.write(output);
            }
        } catch (IOException e) {
            throw new GameRepositoryException(e.getMessage(), e);
        }
        return true;
    }

    private String findOverUnder(List<OverUnder> overUnderList, OverUnder.Type type, double size) {
        return overUnderList.stream()
                .filter(overUnder ->
                        overUnder.getType().equals(type.toString()) && Math.abs(overUnder.getSize() - size) < 0.00001)
                .findFirst()
                .map(overUnder -> new DecimalFormat("#0.00")
                        .format(overUnder.getCoefficient())
                        .replace(",", "."))
                .orElse("");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean createFile(String fileName) {
        try {
            new File("statistics").mkdir();
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new GameRepositoryException(e.getMessage(), e);
        }
    }
}
