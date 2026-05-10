CREATE TABLE ai_interactions (
                                 id UUID PRIMARY KEY,

                                 feature VARCHAR(100) NOT NULL,
                                 model VARCHAR(120),

                                 success BOOLEAN NOT NULL,
                                 http_status INTEGER,

                                 latency_ms BIGINT,

                                 prompt_version VARCHAR(50),

                                 prompt_tokens INTEGER,
                                 completion_tokens INTEGER,
                                 total_tokens INTEGER,

                                 created_at TIMESTAMP NOT NULL,

                                 system_prompt TEXT,
                                 user_prompt TEXT,

                                 request_body TEXT,
                                 raw_response TEXT,
                                 parsed_response TEXT,
                                 error_message TEXT
);