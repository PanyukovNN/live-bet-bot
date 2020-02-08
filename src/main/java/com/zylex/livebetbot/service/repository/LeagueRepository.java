package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.League;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

@Repository
public class LeagueRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public LeagueRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public League save(League league) {
        Session session = sessionFactory.getCurrentSession();
        League retreatedLeague = get(league);
        if (retreatedLeague.getName() == null) {
            Long id = (Long) session.save(league);
            league.setId(id);
            return league;
        } else {
            return retreatedLeague;
        }
    }

    @Transactional
    public League get(League league) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM League WHERE name = :leagueName");
        query.setParameter("leagueName", league.getName());
        try {
            return (League) query.getSingleResult();
        } catch (NoResultException e) {
            return new League();
        }
    }
}
