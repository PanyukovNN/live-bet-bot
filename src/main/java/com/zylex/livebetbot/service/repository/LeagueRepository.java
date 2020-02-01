package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.League;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Repository
public class LeagueRepository {

    private Session session;

    @PostConstruct
    private void postConstruct() {
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (session == null) {
            session = HibernateUtil.getSessionFactory().openSession();
        }
    }

    @PreDestroy
    private void preDestroy() {
        if (session.isOpen()) {
            session.close();
        }
    }

    public void save(League league) {
        session.beginTransaction();
        session.save(league);
        session.getTransaction().commit();
    }

    public void save(List<League> leagues) {
        session.beginTransaction();
        leagues.forEach(session::save);
        session.getTransaction().commit();
    }
}
