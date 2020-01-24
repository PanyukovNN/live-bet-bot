package com.zylex.livebetbot.service.repository;

import com.zylex.livebetbot.model.Game;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.List;
import java.util.Set;

public class GameRepository {

    private Session session;

    public GameRepository(Session session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public List<Game> getNoResultGames() {
        Query query = session.createQuery("FROM Game WHERE finalScore IS NULL OR finalScore = '-1:-1'");
        return query.getResultList();
    }

    public void saveGames(Set<Game> games) {
        session.beginTransaction();
        games.forEach(session::save);
        session.getTransaction().commit();
    }
}
