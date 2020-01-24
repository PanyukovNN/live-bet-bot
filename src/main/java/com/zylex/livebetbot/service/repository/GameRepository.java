package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.Game;
import org.hibernate.Session;

import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class GameRepository {

    private Session session;

    public GameRepository(Session session) {
        this.session = session;
    }

    public List<Game> getWithoutResult() {
        Query query = session.createQuery("FROM Game WHERE finalScore IS NULL OR finalScore = '-1:-1'");
        return query.getResultList();
    }

    public List<Game> getByDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Query query = session.createQuery("FROM Game WHERE dateTime >= :startDateTime AND dateTime <= :endDateTime");
        query.setParameter("startDateTime", startDateTime);
        query.setParameter("endDateTime", endDateTime);
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
}
