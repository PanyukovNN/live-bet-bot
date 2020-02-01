package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.League;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Set;

@Repository
public class LeagueRepository {

    private Session session;

    @PostConstruct
    private void postConstruct() {
        session = HibernateUtil.getSession();
    }

    @PreDestroy
    private void preDestroy() {
        if (session.isOpen()) {
            session.close();
        }
    }

    @Transactional
    public void save(League league) {
        League retreatedLeague = get(league);
        if (retreatedLeague.getName() == null) {
            session.beginTransaction();
            Long id = (Long) session.save(league);
            league.setId(id);
            session.getTransaction().commit();
        } else {
            league.setId(retreatedLeague.getId());
        }
    }

    @Transactional
    public void save(Set<League> leagues) {
        leagues.forEach(this::save);
    }

    @Transactional
    public League get(League league) {
        Query query = session.createQuery("FROM League WHERE name = :leagueName");
        query.setParameter("leagueName", league.getName());
        try {
            return (League) query.getSingleResult();
        } catch (NoResultException e) {
            return new League();
        }
    }
}
