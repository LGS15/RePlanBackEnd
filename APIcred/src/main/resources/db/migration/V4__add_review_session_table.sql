
ALTER TABLE Note DROP FOREIGN KEY Note_ibfk_1;
ALTER TABLE Note DROP COLUMN video_id;

DROP TABLE IF EXISTS Video;

CREATE TABLE ReviewSession (
                               id BINARY(16) PRIMARY KEY,
                               team_id BINARY(16) NOT NULL,
                               video_url VARCHAR(500) NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               description TEXT,
                               current_timestamp BIGINT NOT NULL DEFAULT 0,
                               is_playing BOOLEAN NOT NULL DEFAULT FALSE,
                               created_by BINARY(16) NOT NULL,
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               ended_at DATETIME,
                               status ENUM('ACTIVE', 'ENDED') NOT NULL DEFAULT 'ACTIVE',
                               FOREIGN KEY (team_id) REFERENCES Team(id),
                               FOREIGN KEY (created_by) REFERENCES UserAccount(id)
);

CREATE TABLE ReviewSessionParticipant (
                                          id BINARY(16) PRIMARY KEY,
                                          session_id BINARY(16) NOT NULL,
                                          user_id BINARY(16) NOT NULL,
                                          joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          left_at DATETIME,
                                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                                          FOREIGN KEY (session_id) REFERENCES ReviewSession(id),
                                          FOREIGN KEY (user_id) REFERENCES UserAccount(id),
                                          UNIQUE KEY unique_active_participant (session_id, user_id, is_active)
);


ALTER TABLE Note ADD COLUMN session_id BINARY(16);
ALTER TABLE Note ADD FOREIGN KEY (session_id) REFERENCES ReviewSession(id);