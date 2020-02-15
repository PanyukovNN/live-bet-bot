package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.Game;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
@Transactional
public class GameRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public GameRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Game> getWithoutResult() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE finalScore IS NULL OR finalScore = '-1:-1'");
        return query.getResultList();
    }

    public List<Game> getByDate(LocalDate date) {
        Session session = sessionFactory.getCurrentSession();
        LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
        Query query = session.createQuery("FROM Game WHERE dateTime >= :dayStart AND dateTime <= :dayEnd");
        query.setParameter("dayStart", dayStart);
        query.setParameter("dayEnd", dayEnd);
        return query.getResultList();
    }

    public List<Game> getFromDate(LocalDate date) {
        LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE dateTime >= :dayStart");
        query.setParameter("dayStart", dayStart);
        return query.getResultList();
    }

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

    public List<Game> save(List<Game> games) {
        List<Game> savedGames = new ArrayList<>();
        games.forEach(game -> savedGames.add(save(game)));
        return savedGames;
    }

    public void update(Game game) {
        Session session = sessionFactory.getCurrentSession();
        session.update(game);
    }

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
}
