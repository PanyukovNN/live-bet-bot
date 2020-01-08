package com.zylex.livebetbot.controller.dao;

import com.zylex.livebetbot.exception.OverUnderDaoException;
import com.zylex.livebetbot.model.OverUnder;
import com.zylex.livebetbot.model.OverUnderType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class OverUnderDao {

    private Connection connection;

    public OverUnderDao(Connection connection) {
        this.connection = connection;
    }

    private OverUnder get(OverUnder overUnder) {
        try (PreparedStatement statement = connection.prepareStatement(SQLOverUnder.GET.QUERY)) {
            statement.setLong(1, overUnder.getGameId());
            statement.setString(2, overUnder.getOverUnderType().toString());
            statement.setDouble(3, overUnder.getSize());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extractOverUnder(resultSet);
            }
            return new OverUnder();
        } catch (SQLException e) {
            throw new OverUnderDaoException(e.getMessage(), e);
        }
    }

    public List<OverUnder> getByGameId(long gameId) {
        try (PreparedStatement statement = connection.prepareStatement(SQLOverUnder.GET_BY_GAME_ID.QUERY)) {
            statement.setLong(1, gameId);
            ResultSet resultSet = statement.executeQuery();
            List<OverUnder> overUnderList = new ArrayList<>();
            while (resultSet.next()) {
                overUnderList.add(extractOverUnder(resultSet));
            }
            return overUnderList;
        } catch (SQLException e) {
            throw new OverUnderDaoException(e.getMessage(), e);
        }
    }

    private OverUnder extractOverUnder(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        long gameId = resultSet.getLong("game_id");
        OverUnderType overUnderType = OverUnderType.valueOf(resultSet.getString("type"));
        double size = resultSet.getDouble("size");
        double coefficient = resultSet.getDouble("coefficient");
        return new OverUnder(id, gameId, overUnderType, size, coefficient);
    }

    public void save(OverUnder overUnder) {
        SQLOverUnder sqlRequest = get(overUnder).getId() == 0
                ? SQLOverUnder.INSERT
                : SQLOverUnder.UPDATE;
        try (PreparedStatement statement = connection.prepareStatement(sqlRequest.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, overUnder.getGameId());
            statement.setString(2, overUnder.getOverUnderType().toString());
            statement.setDouble(3, overUnder.getSize());
            statement.setDouble(4, overUnder.getCoefficient());
            if (sqlRequest == SQLOverUnder.UPDATE) {
                statement.setLong(5, overUnder.getId());
            }
            statement.executeUpdate();
            if (sqlRequest == SQLOverUnder.INSERT) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    overUnder.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new OverUnderDaoException(e.getMessage(), e);
        }
    }

    enum SQLOverUnder {
        GET("SELECT * FROM over_under WHERE game_id = (?) AND type = (?) AND size = (?)"),
        GET_BY_GAME_ID("SELECT * FROM over_under WHERE game_id = (?)"),
        INSERT("INSERT INTO over_under (id, game_id, type, size, coefficient) VALUES (DEFAULT, (?), (?), (?), (?))"),
        UPDATE("UPDATE over_under SET game_id = (?), type = (?), size = (?), coefficient = (?) WHERE id = (?)");

        String QUERY;

        SQLOverUnder(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
