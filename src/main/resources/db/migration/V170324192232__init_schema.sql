CREATE TABLE users(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),

    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(64),
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    language_tag VARCHAR(5),
    role VARCHAR(12) NOT NULL,
    activated BOOL,
    activation_key VARCHAR(64),
    reset_key VARCHAR(64),
    reset_at TIMESTAMP,
    auth_provider VARCHAR(32),

    UNIQUE (username),
    UNIQUE (email)
);

CREATE TABLE competitions(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),

    name VARCHAR(50) NOT NULL,
    logo VARCHAR(100),
    rounds VARCHAR(512),
    status VARCHAR(10),

    UNIQUE (name)
);

CREATE TABLE competitors(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),

    name VARCHAR(50) NOT NULL,
    logo VARCHAR(256) NOT NULL,
    competition_id BIGSERIAL NOT NULL,

    UNIQUE (competition_id, name),
    FOREIGN KEY (competition_id) REFERENCES competitions(id)
);

CREATE TABLE matches(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),

    competition_id BIGSERIAL NOT NULL,
    round VARCHAR(25),
    competitor1_id BIGSERIAL NOT NULL,
    competitor2_id BIGSERIAL NOT NULL,
    start_time TIMESTAMP NOT NULL,
    location VARCHAR(100) NOT NULL,
    score1 INT,
    score2 INT,

    FOREIGN KEY (competition_id) REFERENCES competitions(id),
    FOREIGN KEY (competitor1_id) REFERENCES competitors(id),
    FOREIGN KEY (competitor2_id) REFERENCES competitors(id)
);

CREATE TABLE betting_groups(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),

    competition_id BIGSERIAL NOT NULL,
    name VARCHAR(100) NOT NULL,
    rules VARCHAR(1000),
    status VARCHAR(10),
    moderator_id BIGSERIAL NOT NULL,

    UNIQUE (competition_id, name),
    FOREIGN KEY (competition_id) REFERENCES competitions(id),
    FOREIGN KEY (moderator_id) REFERENCES users(id)
);

CREATE TABLE betting_group_players(
    betting_group_id BIGSERIAL NOT NULL,
    player_id BIGSERIAL NOT NULL,

    PRIMARY KEY (betting_group_id, player_id),
    FOREIGN KEY (betting_group_id) REFERENCES betting_groups(id),
    FOREIGN KEY (player_id) REFERENCES users(id)
);

CREATE TABLE betting_matches(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),

    betting_group_id BIGSERIAL NOT NULL,
    match_id BIGSERIAL NOT NULL,
    balance1 DOUBLE PRECISION NOT NULL DEFAULT 0,
    balance2 DOUBLE PRECISION NOT NULL DEFAULT 0,
    expiry_time TIMESTAMP NOT NULL,
    betting_amount DECIMAL(20,2) NOT NULL DEFAULT 0.0,
    comment VARCHAR(1024),
    active BOOL,

    UNIQUE(betting_group_id, match_id),
    FOREIGN KEY (betting_group_id) REFERENCES betting_groups(id),
    FOREIGN KEY (match_id) REFERENCES matches(id)
);

CREATE TABLE betting_players(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    created_by VARCHAR(32),
    modified_at TIMESTAMP,
    modified_by VARCHAR(32),

    betting_match_id BIGSERIAL NOT NULL,
    player_id BIGSERIAL NOT NULL,
    bet_competitor_id BIGSERIAL NOT NULL,

    UNIQUE(betting_match_id, player_id),
    FOREIGN KEY (betting_match_id) REFERENCES betting_matches(id),
    FOREIGN KEY (player_id) REFERENCES users(id),
    FOREIGN KEY (bet_competitor_id) REFERENCES competitors(id)
);
