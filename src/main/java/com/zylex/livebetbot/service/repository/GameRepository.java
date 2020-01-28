package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.exception.GameRepositoryException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.OverUnder;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Repository
public class GameRepository {

    private Session session = HibernateUtil.getSessionFactory().openSession();

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

    public void save(Set<Game> games) {
        session.beginTransaction();
        games.forEach(session::save);
        session.getTransaction().commit();
    }

    public boolean createStatisticsFile(LocalDate date) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String fileName = String.format("statistics/%s.csv", DATE_FORMATTER.format(date));
        if (!createFile(fileName)) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), StandardCharsets.UTF_8))) {
            List<Game> games = getByDate(date);
            if (games.isEmpty()) {
                return false;
            }
            String GAME_BODY_FORMAT = "%s;%s;%s;%s;%s;%s;%s;%s;\n";
            String overUnder1 = "";
            String overUnder15 = "";
            for (Game game : games) {
                for (OverUnder overUnder : game.getOverUnderSet()) {
                    if (overUnder.getType().equals(OverUnder.Type.OVER.toString()) && Math.abs(overUnder.getSize() - 1d) < 0.00001) {
                        overUnder1 = new DecimalFormat("#0.00").format(overUnder.getCoefficient()).replace(",", ".");
                    }
                    if (overUnder.getType().equals(OverUnder.Type.OVER.toString()) && Math.abs(overUnder.getSize() - 1.5d) < 0.00001) {
                        overUnder15 = new DecimalFormat("#0.00").format(overUnder.getCoefficient()).replace(",", ".");
                    }
                }
                String output = String.format(GAME_BODY_FORMAT,
                        game.getDateTime(),
                        game.getFirstTeam(),
                        game.getSecondTeam(),
                        game.getHalfTimeScore(),
                        game.getFinalScore(),
                        game.getRuleNumber(),
                        overUnder1,
                        overUnder15);
                writer.write(output);
            }
        } catch (IOException e) {
            throw new GameRepositoryException(e.getMessage(), e);
        }
        return true;
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
