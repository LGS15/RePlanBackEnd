CREATE TABLE PracticePlanRequest (
                                     id BINARY(16) PRIMARY KEY,
                                     user_id BINARY(16) NOT NULL,
                                     team_id BINARY(16),
                                     practice_type ENUM('INDIVIDUAL', 'TEAM') NOT NULL,
                                     available_hours INT NOT NULL,
                                     focus_priority_1 ENUM(
        'AIM_TRAINING',
        'GAME_SENSE', 
        'MOVEMENT_MECHANICS', 
        'MAP_KNOWLEDGE', 
        'TEAM_COORDINATION', 
        'STRATEGY_REVIEW', 
        'VOD_ANALYSIS', 
        'COMMUNICATION', 
        'POST_PLANT_SCENARIOS',
        'SITE_HOLDS'
    ),
                                     focus_priority_2 ENUM(
        'AIM_TRAINING', 
        'GAME_SENSE', 
        'MOVEMENT_MECHANICS', 
        'MAP_KNOWLEDGE', 
        'TEAM_COORDINATION', 
        'STRATEGY_REVIEW', 
        'VOD_ANALYSIS', 
        'COMMUNICATION', 
        'POST_PLANT_SCENARIOS',
        'SITE_HOLDS'
    ),
                                     focus_priority_3 ENUM(
        'AIM_TRAINING', 
        'GAME_SENSE', 
        'MOVEMENT_MECHANICS', 
        'MAP_KNOWLEDGE', 
        'TEAM_COORDINATION', 
        'STRATEGY_REVIEW', 
        'VOD_ANALYSIS', 
        'COMMUNICATION', 
        'POST_PLANT_SCENARIOS',
        'SITE_HOLDS'
    ),
                                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (user_id) REFERENCES UserAccount(id),
                                     FOREIGN KEY (team_id) REFERENCES Team(id)
);

CREATE TABLE PracticePlan (
                              id BINARY(16) PRIMARY KEY,
                              request_id BINARY(16) NOT NULL,
                              focus_one_hours DECIMAL(4,2),
                              focus_two_hours DECIMAL(4,2),
                              focus_three_hours DECIMAL(4,2),
                              total_hours INT NOT NULL,
                              practice_type ENUM('INDIVIDUAL', 'TEAM') NOT NULL,
                              generated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (request_id) REFERENCES PracticePlanRequest(id)
);