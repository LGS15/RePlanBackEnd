CREATE TABLE Video (
                       id BINARY(16) PRIMARY KEY,
                       team_id BINARY(16) NOT NULL,
                       url VARCHAR(255) NOT NULL,
                       uploaded_at DATETIME NOT NULL,
                       uploaded_by BINARY(16) NOT NULL,
                       FOREIGN KEY (team_id) REFERENCES Team(id),
                       FOREIGN KEY (uploaded_by) REFERENCES UserAccount(id)
);

CREATE TABLE Note (
                      id BINARY(16) PRIMARY KEY,
                      video_id BINARY(16) NOT NULL,
                      author_id BINARY(16) NOT NULL,
                      timestamp BIGINT NOT NULL,
                      content TEXT NOT NULL,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME,
                      FOREIGN KEY (video_id) REFERENCES Video(id),
                      FOREIGN KEY (author_id) REFERENCES UserAccount(id)
);

CREATE TABLE Availability (
                              id BINARY(16) PRIMARY KEY,
                              user_id BINARY(16) NOT NULL,
                              team_id BINARY(16) NOT NULL,
                              day_of_week INT NOT NULL,
                              start_time TIME NOT NULL,
                              end_time TIME NOT NULL,
                              recurring BOOLEAN NOT NULL,
                              FOREIGN KEY (user_id) REFERENCES UserAccount(id),
                              FOREIGN KEY (team_id) REFERENCES Team(id)
);
