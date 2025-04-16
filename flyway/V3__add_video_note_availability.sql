CREATE TABLE Video (
                       id VARCHAR(36) PRIMARY KEY,
                       team_id VARCHAR(36) NOT NULL,
                       title VARCHAR(100) NOT NULL,
                       uploaded_at DATETIME NOT NULL,
                       uploaded_by VARCHAR(36) NOT NULL,
                       FOREIGN KEY (team_id) REFERENCES Team(id),
                       FOREIGN KEY (uploaded_by) REFERENCES UserAccount(id)
);

CREATE TABLE Note (
                      id VARCHAR(36) PRIMARY KEY,
                      video_id VARCHAR(36) NOT NULL,
                      author_id VARCHAR(36) NOT NULL,
                      timestamp BIGINT NOT NULL,
                      content TEXT NOT NULL,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME,
                      FOREIGN KEY (video_id) REFERENCES Video(id),
                      FOREIGN KEY (author_id) REFERENCES UserAccount(id)
);

CREATE TABLE Availability (
                              id VARCHAR(36) PRIMARY KEY,
                              user_id VARCHAR(36) NOT NULL,
                              team_id VARCHAR(36) NOT NULL,
                              day_of_week INT NOT NULL,
                              start_time TIME NOT NULL,
                              end_time TIME NOT NULL,
                              recurring BOOLEAN NOT NULL,
                              FOREIGN KEY (user_id) REFERENCES UserAccount(id),
                              FOREIGN KEY (team_id) REFERENCES Team(id)
);
