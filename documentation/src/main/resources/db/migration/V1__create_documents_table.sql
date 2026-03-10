CREATE TABLE documents
(
    id VARCHAR(36) PRIMARY KEY,
    application_id VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    file_name VARCHAR(255),
    storage_path VARCHAR(500),
    uploaded_at TIMESTAMP,
    status VARCHAR(30)
);