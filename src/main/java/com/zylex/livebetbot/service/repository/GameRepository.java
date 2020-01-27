package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.Game;
import org.hibernate.Session;

import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class GameRepository {

//    private SessionFactory sessionFactory;

    private Session session;

    public GameRepository(Session session) {
        this.session = session;
    }

    public List<Game> getWithoutResult() {
//        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE finalScore IS NULL OR finalScore = '-1:-1'");
        return query.getResultList();
    }

    public List<Game> getByDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
//        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Game WHERE dateTime >= :startDateTime AND dateTime <= :endDateTime");
        query.setParameter("startDateTime", startDateTime);
        query.setParameter("endDateTime", endDateTime);
        return query.getResultList();
    }

    public void save(Game game) {
//        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(game);
        session.getTransaction().commit();
    }

    public void save(Set<Game> games) {
//        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        games.forEach(session::save);
        session.getTransaction().commit();
    }
}
