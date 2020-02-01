package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.Country;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Repository
public class CountryRepository {

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

    public void save(Country country) {
        session.beginTransaction();
        session.save(country);
        session.getTransaction().commit();
    }

    public void save(List<Country> countries) {
        session.beginTransaction();
        countries.forEach(session::save);
        session.getTransaction().commit();
    }
}
