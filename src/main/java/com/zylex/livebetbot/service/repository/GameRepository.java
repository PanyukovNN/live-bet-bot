package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.exception.GameRepositoryException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.OverUnder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class GameRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public GameRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public List<Game> getWithoutResult() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE finalScore IS NULL OR finalScore = '-1:-1'");
        return query.getResultList();
    }

    @Transactional
    public List<Game> getByDate(LocalDate date) {
        LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE dateTime >= :dayStart AND dateTime <= :dayEnd");
        query.setParameter("dayStart", dayStart);
        query.setParameter("dayEnd", dayEnd);
        return query.getResultList();
    }

    @Transactional
    public List<Game> getFromDate(LocalDate date) {
        LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE dateTime >= :dayStart");
        query.setParameter("dayStart", dayStart);
        return query.getResultList();
    }

    @Transactional
    public Game save(Game game) {
        Session session = sessionFactory.getCurrentSession();
        Game retreatedGame = get(game);
        if (retreatedGame.getLink() == null) {
            Long id = (Long) session.save(game);
            game.setId(id);
            return game;
        } else {
            return retreatedGame;
        }
    }

    @Transactional
    public List<Game> save(List<Game> games) {
        List<Game> savedGames = new ArrayList<>();
        games.forEach(game -> savedGames.add(save(game)));
        return savedGames;
    }

    @Transactional
    public void update(Game game) {
        Session session = sessionFactory.getCurrentSession();
        session.update(game);
    }

    @Transactional
    public Game get(Game game) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE ruleNumber = :ruleNumber AND link = :link");
        query.setParameter("ruleNumber", game.getRuleNumber());
        query.setParameter("link", game.getLink());
        try {
            return (Game) query.getSingleResult();
        } catch (NoResultException e) {
            return new Game();
        }
    }

    public boolean createStatisticsFile(LocalDate date) {
        List<Game> games = getByDate(date);
        if (games.isEmpty()) {
            return false;
        }
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String fileName = String.format("statistics/%s.csv", DATE_FORMATTER.format(date));
        createFile(fileName);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), StandardCharsets.UTF_8))) {
            String GAME_BODY_FORMAT = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n";
            for (Game game : games) {
                String over10 = findOverUnder(game.getOverUnderList(), OverUnder.Type.OVER, 1);
                String over15 = findOverUnder(game.getOverUnderList(), OverUnder.Type.OVER, 1.5);
                String under10 = findOverUnder(game.getOverUnderList(), OverUnder.Type.UNDER, 1);
                String under15 = findOverUnder(game.getOverUnderList(), OverUnder.Type.UNDER, 1.5);
                String output = String.format(GAME_BODY_FORMAT,
                        DATE_FORMATTER.format(game.getDateTime()),
                        game.getCountry().getName(),
                        game.getLeague().getName(),
                        game.getFirstTeam(), game.getSecondTeam(),
                        game.getScanTimeScore(), game.getFinalScore(),
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
    private void createFile(String fileName) {
        try {
            new File("statistics").mkdir();
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new GameRepositoryException(e.getMessage(), e);
        }
    }
}
