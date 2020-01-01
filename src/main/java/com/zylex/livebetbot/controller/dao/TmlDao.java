package com.zylex.livebetbot.controller.dao;

import com.zylex.livebetbot.exception.GameDaoException;
import com.zylex.livebetbot.model.MoreLess;
import com.zylex.livebetbot.model.Tml;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class TmlDao {

    private Connection connection;

    public TmlDao(Connection connection) {
        this.connection = connection;
    }

    private Tml get(Tml tml) {
        try (PreparedStatement statement = connection.prepareStatement(SQLTml.GET.QUERY)) {
            statement.setLong(1, tml.getGameId());
            statement.setString(2, tml.getMoreLess().toString());
            statement.setDouble(3, tml.getSize());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extractTml(resultSet);
            }
            return new Tml();
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    public List<Tml> getByGameId(long gameId) {
        try (PreparedStatement statement = connection.prepareStatement(SQLTml.GET_BY_GAME_ID.QUERY)) {
            statement.setLong(1, gameId);
            ResultSet resultSet = statement.executeQuery();
            List<Tml> tmlList = new ArrayList<>();
            while (resultSet.next()) {
                tmlList.add(extractTml(resultSet));
            }
            return tmlList;
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    private Tml extractTml(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        long gameId = resultSet.getLong("game_id");
        MoreLess moreLess = MoreLess.valueOf(resultSet.getString("more_less"));
        double size = resultSet.getDouble("size");
        double coefficient = resultSet.getDouble("coefficient");
        return new Tml(id, gameId, moreLess, size, coefficient);
    }

    public void save(Tml tml) {
        SQLTml sqlRequest = get(tml).getId() == 0
                ? SQLTml.INSERT
                : SQLTml.UPDATE;
        try (PreparedStatement statement = connection.prepareStatement(sqlRequest.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, tml.getGameId());
            statement.setString(2, tml.getMoreLess().toString());
            statement.setDouble(3, tml.getSize());
            statement.setDouble(4, tml.getCoefficient());
            if (sqlRequest == SQLTml.UPDATE) {
                statement.setLong(5, tml.getId());
            }
            statement.executeUpdate();
            if (sqlRequest == SQLTml.INSERT) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tml.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    enum SQLTml {
        GET("SELECT * FROM tml WHERE game_id = (?) AND more_less = (?) AND size = (?)"),
        GET_BY_GAME_ID("SELECT * FROM tml WHERE game_id = (?)"),
        INSERT("INSERT INTO tml (id, game_id, more_less, size, coefficient) VALUES (DEFAULT, (?), (?), (?), (?))"),
        UPDATE("UPDATE tml SET game_id = (?), more_less = (?), size = (?), coefficient = (?) WHERE id = (?)");

        String QUERY;

        SQLTml(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
