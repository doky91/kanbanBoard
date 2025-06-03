-- table status
CREATE TABLE status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
-- populate status
INSERT INTO status (name) VALUES ('TO_DO'), ('IN_PROGRESS'), ('DONE');
