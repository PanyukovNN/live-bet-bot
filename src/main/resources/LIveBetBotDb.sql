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

DROP TABLE IF EXISTS over_under CASCADE;
CREATE TABLE IF NOT EXISTS over_under (
    id          SERIAL NOT NULL PRIMARY KEY,
    game_id     BIGINT NOT NULL,
    type        VARCHAR(20),
    size        FLOAT,
    coefficient FLOAT,
    FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE
);
