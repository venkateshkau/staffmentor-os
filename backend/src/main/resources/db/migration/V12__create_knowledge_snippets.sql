CREATE TABLE knowledge_snippets (
    id UUID PRIMARY KEY,
    content TEXT NOT NULL,
    next_review_date DATE NOT NULL,
    interval_days INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
