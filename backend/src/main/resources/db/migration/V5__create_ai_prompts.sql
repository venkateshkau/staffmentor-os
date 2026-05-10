CREATE TABLE ai_prompts (
                            id UUID PRIMARY KEY,

                            feature VARCHAR(100) NOT NULL,
                            prompt_type VARCHAR(50) NOT NULL,

                            version INTEGER NOT NULL,

                            active BOOLEAN NOT NULL DEFAULT FALSE,
                            deleted BOOLEAN NOT NULL DEFAULT FALSE,

                            created_by VARCHAR(120),

                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP,

                            content TEXT NOT NULL,
                            notes TEXT
);

CREATE INDEX idx_ai_prompts_lookup
    ON ai_prompts(feature, prompt_type, active, deleted);