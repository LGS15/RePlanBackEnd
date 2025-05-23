
CREATE TABLE UserAccount (
                             id BINARY(16) PRIMARY KEY,
                             username VARCHAR(50) NOT NULL UNIQUE,
                             email VARCHAR(100) NOT NULL UNIQUE,
                             password VARCHAR(100) NOT NULL
);

CREATE TABLE Team (
                      id BINARY(16) PRIMARY KEY,
                      team_name VARCHAR(100) NOT NULL,
                      game_name VARCHAR(100) NOT NULL,
                      owner_id BINARY(16) NOT NULL,
                      FOREIGN KEY (owner_id) REFERENCES UserAccount(id)
);

CREATE TABLE TeamMember (
                            id BINARY(16) PRIMARY KEY,
                            team_id BINARY(16) NOT NULL,
                            user_id BINARY(16) NOT NULL,
                            role ENUM('OWNER', 'PLAYER', 'COACH') NOT NULL,
                            FOREIGN KEY (team_id) REFERENCES Team(id),
                            FOREIGN KEY (user_id) REFERENCES UserAccount(id)
);