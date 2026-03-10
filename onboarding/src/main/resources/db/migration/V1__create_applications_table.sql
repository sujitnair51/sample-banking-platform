CREATE TABLE applications
(
    id VARCHAR(36) PRIMARY KEY,

    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,

    status VARCHAR(30) NOT NULL
);

CREATE UNIQUE INDEX idx_app_email
ON applications(email);