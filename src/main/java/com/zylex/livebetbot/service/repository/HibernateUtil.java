package com.zylex.livebetbot.service.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

@SuppressWarnings("WeakerAccess")
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    private static Session session;

    static {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        session = sessionFactory.openSession();
    }

    public static synchronized Session getSession() {
        return session;
    }
}
