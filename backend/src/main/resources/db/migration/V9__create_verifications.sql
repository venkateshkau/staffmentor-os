CREATE TABLE verifications (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES coach_sessions(id) ON DELETE CASCADE UNIQUE,
    submission_text TEXT NOT NULL,
    mastery_level VARCHAR(50) NOT NULL,
    confidence_score INTEGER NOT NULL,
    ai_feedback TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
