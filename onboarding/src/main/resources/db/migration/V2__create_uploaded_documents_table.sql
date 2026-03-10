CREATE TABLE uploaded_documents
(
    id UUID PRIMARY KEY,

    application_id VARCHAR(36) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_uploaded_docs_app
ON uploaded_documents(application_id);