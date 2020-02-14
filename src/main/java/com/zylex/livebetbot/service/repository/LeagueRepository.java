package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.League;
import com.zylex.livebetbot.model.LeagueToScan;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional
public class LeagueRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public LeagueRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

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

    @SuppressWarnings("unchecked")
    public List<LeagueToScan> getLeaguesToScan() {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM LeagueToScan");
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }
}
