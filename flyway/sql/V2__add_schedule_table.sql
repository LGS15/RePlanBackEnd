CREATE TABLE Schedule (
                          id VARCHAR(36) PRIMARY KEY,
                          team_id VARCHAR(36) NOT NULL,
                          title VARCHAR(100) NOT NULL,
                          description TEXT,
                          start_time DATETIME NOT NULL,
                          end_time DATETIME NOT NULL,
                          created_by VARCHAR(36) NOT NULL,
                          FOREIGN KEY (team_id) REFERENCES Team(id),
                          FOREIGN KEY (created_by) REFERENCES UserAccount(id)
);
