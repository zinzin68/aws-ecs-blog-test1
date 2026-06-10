DROP TABLE IF EXISTS files;

CREATE TABLE files
(
    id           BIGSERIAL PRIMARY KEY,
    file_name    VARCHAR(255) NOT NULL,
    description  VARCHAR(500),
    content_type VARCHAR(100) NOT NULL,
    storage_key  VARCHAR(500) NOT NULL UNIQUE,   -- S3 key or 로컬 파일명
    size         BIGINT       NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
