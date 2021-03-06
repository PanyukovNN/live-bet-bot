DROP TABLE IF EXISTS game CASCADE;
CREATE TABLE IF NOT EXISTS game (
    id                  BIGSERIAL NOT NULL PRIMARY KEY,
    date_time           TIMESTAMP NOT NULL,
    game_time           VARCHAR(10),
    country_id          BIGSERIAL,
    league_id           BIGSERIAL,
    first_team          VARCHAR(50) NOT NULL,
    second_team         VARCHAR(50) NOT NULL,
    scan_time_score     VARCHAR(10) NOT NULL,
    final_score         VARCHAR(10),
    rule_number         VARCHAR(20),
    link                VARCHAR(300),
    FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE SET NULL,
    FOREIGN KEY (league_id) REFERENCES league(id) ON DELETE SET NULL,
    CONSTRAINT UC_game UNIQUE (rule_number, link)
);

DROP TABLE IF EXISTS over_under CASCADE;
CREATE TABLE IF NOT EXISTS over_under (
    id          BIGSERIAL PRIMARY KEY,
    game_id     BIGSERIAL,
    type        VARCHAR(20),
    size        FLOAT,
    coefficient FLOAT,
    FOREIGN KEY (game_id) REFERENCES game(id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS country CASCADE;
CREATE TABLE IF NOT EXISTS country (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    link    VARCHAR(300) NOT NULL,
    CONSTRAINT UC_country UNIQUE (name)
);

DROP TABLE IF EXISTS league CASCADE;
CREATE TABLE IF NOT EXISTS league (
    id          BIGSERIAL PRIMARY KEY,
    country_id  BIGSERIAL,
    name        VARCHAR(300) NOT NULL,
    is_new      BOOLEAN,
    FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE SET NULL,
    CONSTRAINT UC_league UNIQUE (country_id, name)
);
