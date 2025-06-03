-- table priority
CREATE TABLE priority (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- populate priority
INSERT INTO priority(name) VALUES ('LOW'), ('MED'), ('HIGH');
