CREATE TABLE sprints (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    jd_text TEXT NOT NULL,
    target_date DATE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

ALTER TABLE goals ADD COLUMN sprint_id UUID REFERENCES sprints(id) ON DELETE SET NULL;
