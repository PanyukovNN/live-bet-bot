package com.zylex.livebetbot.controller.dao;

import com.zylex.livebetbot.exception.GameDaoException;
import com.zylex.livebetbot.model.Game;
import com.zylex.livebetbot.model.Goal;
import com.zylex.livebetbot.service.rule.RuleNumber;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GameDao {

    private final Connection connection;

    private final TmlDao tmlDao;

    public GameDao(final Connection connection) {
        this.connection = connection;
        this.tmlDao = new TmlDao(connection);
    }

    private Game get(Game game) {
        try (PreparedStatement statement = connection.prepareStatement(SQLGame.GET.QUERY)) {
            statement.setDate(1, Date.valueOf(game.getDate()));
            statement.setString(2, game.getFirstTeam());
            statement.setString(3, game.getSecondTeam());
            statement.setString(4, game.getRuleNumber().toString());
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
        LocalDate dateTime = resultSet.getDate("date_time").toLocalDate();
        String firstTeam = resultSet.getString("first_team");
        String secondTeam = resultSet.getString("second_team");
        int homeGoalBreak = resultSet.getInt("home_goal_break");
        int awayGoalBreak = resultSet.getInt("away_goal_break");
        Goal breakGoal = new Goal(homeGoalBreak, awayGoalBreak);
        int homeGoalFinal = resultSet.getInt("home_goal_final");
        int awayGoalFinal = resultSet.getInt("away_goal_final");
        Goal finalGoal = new Goal(homeGoalFinal, awayGoalFinal);
        RuleNumber ruleNumber = RuleNumber.valueOf(resultSet.getString("rule_number"));
        String link = resultSet.getString("link");
        Game extractedGame = new Game(id, dateTime, firstTeam, secondTeam, link);
        extractedGame.setBreakGoal(breakGoal);
        extractedGame.setFinalGoal(finalGoal);
        extractedGame.setRuleNumber(ruleNumber);
        extractedGame.setTmlList(tmlDao.getByGameId(id));
        return extractedGame;
    }

    public void save(Game game) {
        SQLGame sqlRequest = get(game).getId() == 0
                ? SQLGame.INSERT
                : SQLGame.UPDATE;
        try (PreparedStatement statement = connection.prepareStatement(sqlRequest.QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setDate(1, Date.valueOf(game.getDate()));
            statement.setString(2, game.getFirstTeam());
            statement.setString(3, game.getSecondTeam());
            statement.setInt(4, game.getBreakGoal().getHomeGoals());
            statement.setInt(5, game.getBreakGoal().getAwayGoals());
            statement.setInt(6, game.getFinalGoal().getAwayGoals());
            statement.setInt(7, game.getFinalGoal().getAwayGoals());
            statement.setString(8, game.getRuleNumber().toString());
            statement.setString(9, game.getLink());
            if (sqlRequest == SQLGame.UPDATE) {
                statement.setLong(10, game.getId());
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

    enum SQLGame {
        GET("SELECT * FROM game WHERE date_time = (?) AND first_team = (?) AND second_team = (?) AND rule_number = (?)"),
        GET_WITH_NO_RESULT("SELECT * FROM game WHERE home_goal_final = -1 AND away_goal_final = -1"),
        INSERT("INSERT INTO game (id, date_time, first_team, second_team, home_goal_break, away_goal_break, home_goal_final, away_goal_final, rule_number, link) VALUES (DEFAULT, (?), (?), (?), (?), (?), (?), (?), (?), (?))"),
        UPDATE("UPDATE game SET date_time = (?), first_team = (?), second_team = (?), home_goal_break = (?), away_goal_break = (?), home_goal_final = (?), away_goal_final = (?), rule_number = (?), link = (?) WHERE id = (?)");

        String QUERY;

        SQLGame(String QUERY) {
            this.QUERY = QUERY;
        }
    }
}
