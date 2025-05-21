CREATE TABLE Schedule (
                          id BINARY(16) PRIMARY KEY,
                          team_id BINARY(16) NOT NULL,
                          title VARCHAR(100) NOT NULL,
                          description TEXT,
                          start_time DATETIME NOT NULL,
                          end_time DATETIME NOT NULL,
                          created_by BINARY(16) NOT NULL,
                          FOREIGN KEY (team_id) REFERENCES Team(id),
                          FOREIGN KEY (created_by) REFERENCES UserAccount(id)
);
