package com.zylex.livebetbot.controller.dao;

import com.zylex.livebetbot.exception.GameDaoException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.Goal;
import com.zylex.livebetbot.service.rule.RuleNumber;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameDao {

    private final Connection connection;

    private final TmlDao tmlDao;

    public GameDao(final Connection connection) {
        this.connection = connection;
        this.tmlDao = new TmlDao(connection);
    }

    public List<Game> getAll() {
        try (PreparedStatement statement = connection.prepareStatement(SQLGame.GET_ALL.QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            List<Game> games = new ArrayList<>();
            while (resultSet.next()) {
                games.add(extractGame(resultSet));
            }
            return games;
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    public List<Game> getByDate(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try (PreparedStatement statement = connection.prepareStatement(SQLGame.GET_BY_DATE.QUERY)) {
            statement.setTimestamp(1, Timestamp.valueOf(startDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(endDateTime));
            ResultSet resultSet = statement.executeQuery();
            List<Game> games = new ArrayList<>();
            while (resultSet.next()) {
                games.add(extractGame(resultSet));
            }
            return games;
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    private Game get(Game game) {
        try (PreparedStatement statement = connection.prepareStatement(SQLGame.GET.QUERY)) {
            statement.setString(1, game.getRuleNumber().toString());
            statement.setString(2, game.getLink());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extractGame(resultSet);
            }
            return new Game();
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    public List<Game> getNoResultGames() {
        try (PreparedStatement statement = connection.prepareStatement(SQLGame.GET_WITH_NO_RESULT.QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            List<Game> extractedGames = new ArrayList<>();
            while (resultSet.next()) {
                extractedGames.add(extractGame(resultSet));
            }
            return extractedGames;
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    private Game extractGame(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        LocalDateTime dateTime = resultSet.getTimestamp("date_time").toLocalDateTime();
        String firstTeam = resultSet.getString("first_team");
        String secondTeam = resultSet.getString("second_team");
        Goal breakScore = stringToGoal(resultSet.getString("break_score"));
        Goal finalScore = stringToGoal(resultSet.getString("final_score"));
        RuleNumber ruleNumber = RuleNumber.valueOf(resultSet.getString("rule_number"));
        String link = resultSet.getString("link");
        Game extractedGame = new Game(id, dateTime, firstTeam, secondTeam, link);
        extractedGame.setBreakGoal(breakScore);
        extractedGame.setFinalGoal(finalScore);
        extractedGame.setRuleNumber(ruleNumber);
        extractedGame.setTmlList(tmlDao.getByGameId(id));
        return extractedGame;
    }

    public void save(Game game) {
        SQLGame sqlRequest = get(game).getId() == 0
                ? SQLGame.INSERT
                : SQLGame.UPDATE;
        try (PreparedStatement statement = connection.prepareStatement(sqlRequest.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setTimestamp(1, Timestamp.valueOf(game.getDateTime()));
            statement.setString(2, game.getFirstTeam());
            statement.setString(3, game.getSecondTeam());
            statement.setString(4, game.getBreakGoal().toString());
            statement.setString(5, game.getFinalGoal().toString());
            statement.setString(6, game.getRuleNumber().toString());
            statement.setString(7, game.getLink());
            if (sqlRequest == SQLGame.UPDATE) {
                statement.setLong(8, game.getId());
            }
            statement.executeUpdate();
            if (sqlRequest == SQLGame.INSERT) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    game.setId(generatedKeys.getInt(1));
                    game.getTmlList().forEach(tml -> tml.setGameId(game.getId()));
                }
            }
            game.getTmlList().forEach(tmlDao::save);
        } catch (SQLException e) {
            throw new GameDaoException(e.getMessage(), e);
        }
    }

    private Goal stringToGoal(String input) {
        if (input == null) {
            return new Goal(-1, -1);
        }
        String[] goals = input.split(":");
        int homeGoal = Integer.parseInt(goals[0]);
        int awayGoal = Integer.parseInt(goals[1]);
        return new Goal(homeGoal, awayGoal);
    }

    enum SQLGame {
        GET("SELECT * FROM game WHERE rule_number = (?) AND link = (?)"),
        GET_ALL("SELECT * FROM game"),
        GET_BY_DATE("SELECT * FROM game WHERE date_time >= (?) AND date_time <= (?)"),
        GET_WITH_NO_RESULT("SELECT * FROM game WHERE final_score IS NULL OR final_score = '-1:-1'"),
        INSERT("INSERT INTO game (id, date_time, first_team, second_team, break_score, final_score, rule_number, link) VALUES (DEFAULT, (?), (?), (?), (?), (?), (?), (?))"),
        UPDATE("UPDATE game SET date_time = (?), first_team = (?), second_team = (?), break_score = (?), final_score = (?), rule_number = (?), link = (?) WHERE id = (?)");

        String QUERY;

        SQLGame(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
