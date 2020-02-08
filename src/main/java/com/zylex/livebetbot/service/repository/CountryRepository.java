package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Set;

@Repository
public class CountryRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CountryRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public Country save(Country country) {
        Session session = sessionFactory.getCurrentSession();
        Country retreatedCountry = get(country);
        if (retreatedCountry.getName() == null) {
            Long id = (Long) session.save(country);
            country.setId(id);
            return country;
        } else {
            return retreatedCountry;
        }
    }

    @Transactional
    public Set<Country> save(Set<Country> countries) {
        Set<Country> savedCountries = new LinkedHashSet<>();
        countries.forEach(country -> savedCountries.add(save(country)));
        return savedCountries;
    }

    @Transactional
    public Country get(Country country) {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery("FROM Country WHERE name = :countryName");
        query.setParameter("countryName", country.getName());
        try {
            return (Country) query.getSingleResult();
        } catch (NoResultException e) {
            return new Country();
        }
    }
}
