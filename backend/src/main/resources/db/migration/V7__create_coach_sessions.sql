CREATE TABLE coach_sessions (
                                id UUID PRIMARY KEY,

                                created_at TIMESTAMP NOT NULL,

                                energy_level INTEGER,
                                available_minutes INTEGER,

                                completed_yesterday BOOLEAN,

                                blockers TEXT,
                                upcoming_interview TEXT,
                                additional_notes TEXT,

                                today_focus TEXT,

                                action_items TEXT,

                                calendar_suggestions TEXT,

                                follow_up_question TEXT,

                                motivation TEXT,

                                completed BOOLEAN NOT NULL DEFAULT FALSE
);