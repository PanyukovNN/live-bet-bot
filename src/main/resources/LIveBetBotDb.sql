DROP TABLE IF EXISTS game CASCADE;
CREATE TABLE IF NOT EXISTS game (
    id                BIGSERIAL NOT NULL PRIMARY KEY,
    date_time         TIMESTAMP NOT NULL,
    first_team        VARCHAR(50) NOT NULL,
    second_team       VARCHAR(50) NOT NULL,
    break_score       VARCHAR(20) NOT NULL,
    final_score       VARCHAR(20),
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

SELECT date_time, first_team, second_team, break_score, final_score, rule_number,
       (SELECT coefficient
           FROM tml
           WHERE size = 1 AND more_less = 'MORE' AND game_id = game.id),
       (SELECT coefficient
           FROM tml
           WHERE  size = 1.5 AND more_less = 'MORE' AND game_id = game.id)
FROM game
WHERE date_time > '2020-01-07 00:00:00.000000' AND date_time < '2020-01-07 23:59:99.999999';
