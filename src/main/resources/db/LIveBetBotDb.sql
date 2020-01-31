DROP TABLE IF EXISTS game CASCADE;
CREATE TABLE IF NOT EXISTS game (
    id                BIGSERIAL NOT NULL PRIMARY KEY,
    date_time         TIMESTAMP NOT NULL,
    country           VARCHAR(100) NOT NULL,
    league            VARCHAR(300),
    first_team        VARCHAR(50) NOT NULL,
    second_team       VARCHAR(50) NOT NULL,
    half_time_score   VARCHAR(20) NOT NULL,
    final_score       VARCHAR(20),
    rule_number       VARCHAR(100),
    link              VARCHAR(500)
);

DROP TABLE IF EXISTS over_under CASCADE;
CREATE TABLE IF NOT EXISTS over_under (
    id          BIGSERIAL NOT NULL PRIMARY KEY,
    game_id     BIGSERIAL,
    type        VARCHAR(20),
    size        FLOAT,
    coefficient FLOAT,
    FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS league_to_scan CASCADE;
CREATE TABLE IF NOT EXISTS league_to_scan (
    id      BIGSERIAL NOT NULL PRIMARY KEY,
    league  VARCHAR(300) NOT NULL
);
