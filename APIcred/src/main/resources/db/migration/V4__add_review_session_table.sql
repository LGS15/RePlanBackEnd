SET FOREIGN_KEY_CHECKS = 0;

-- Drop FK on Note.video_id if exists
SET @fk_name = (
  SELECT CONSTRAINT_NAME
  FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'Note'
    AND COLUMN_NAME = 'video_id'
    AND REFERENCED_TABLE_NAME IS NOT NULL
  LIMIT 1
);

SET @sql = IF(@fk_name IS NOT NULL, CONCAT('ALTER TABLE Note DROP FOREIGN KEY ', @fk_name), 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Now drop video_id column if it exists
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'Note'
     AND COLUMN_NAME = 'video_id') > 0,
    'ALTER TABLE Note DROP COLUMN video_id',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Drop the old Video table if it exists
DROP TABLE IF EXISTS Video;

SET FOREIGN_KEY_CHECKS = 1;

-- Create ReviewSession table
CREATE TABLE ReviewSession (
                               id BINARY(16) PRIMARY KEY,
                               team_id BINARY(16) NOT NULL,
                               video_url VARCHAR(500) NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               description TEXT,
                               video_timestamp BIGINT NOT NULL DEFAULT 0,
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

-- Add session_id column to Note table if it doesn't exist
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'Note'
     AND COLUMN_NAME = 'session_id') = 0,
    'ALTER TABLE Note ADD COLUMN session_id BINARY(16)',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add foreign key constraint if it doesn't exist
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
     WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'Note'
     AND CONSTRAINT_NAME = 'fk_note_session') = 0,
    'ALTER TABLE Note ADD CONSTRAINT fk_note_session FOREIGN KEY (session_id) REFERENCES ReviewSession(id)',
    'SELECT 1'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;