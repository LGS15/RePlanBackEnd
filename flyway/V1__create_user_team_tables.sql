CREATE TABLE UserAccount (
                             id VARCHAR(36) PRIMARY KEY,
                             username VARCHAR(50) NOT NULL UNIQUE,
                             email VARCHAR(100) NOT NULL UNIQUE,
                             password VARCHAR(100) NOT NULL
);

CREATE TABLE Team (
                      id VARCHAR(36) PRIMARY KEY,
                      team_name VARCHAR(100) NOT NULL,
                      game_name VARCHAR(100) NOT NULL,
                      owner_id VARCHAR(36) NOT NULL,
                      FOREIGN KEY (owner_id) REFERENCES UserAccount(id)
);

CREATE TABLE TeamMember (
                            id VARCHAR(36) PRIMARY KEY,
                            team_id VARCHAR(36) NOT NULL,
                            user_id VARCHAR(36) NOT NULL,
                            role ENUM('CAPTAIN', 'PLAYER', 'COACH', 'ANALYST') NOT NULL,
                            FOREIGN KEY (team_id) REFERENCES Team(id),
                            FOREIGN KEY (user_id) REFERENCES UserAccount(id)
);
