DROP TABLE IF EXISTS game CASCADE;
CREATE TABLE IF NOT EXISTS game (
    id                BIGSERIAL NOT NULL PRIMARY KEY,
    date_time         TIMESTAMP NOT NULL,
    first_team        VARCHAR(50) NOT NULL,
    second_team       VARCHAR(50) NOT NULL,
    home_goal_break   INT NOT NULL,
    away_goal_break   INT NOT NULL,
    home_goal_final   INT,
    away_goal_final   INT,
    rule_number       VARCHAR(100),
    link              VARCHAR(500)
);

DROP TABLE IF EXISTS tml CASCADE;
CREATE TABLE IF NOT EXISTS tml (
    id          SERIAL NOT NULL PRIMARY KEY,
    game_id     BIGINT NOT NULL,
    more_less   VARCHAR(20),
    size        FLOAT,
    coefficient FLOAT,
    FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);

