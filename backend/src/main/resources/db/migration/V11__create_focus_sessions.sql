CREATE TABLE focus_sessions (
    id UUID PRIMARY KEY,
    action_item_id UUID NOT NULL REFERENCES coach_session_action_items(id) ON DELETE CASCADE,
    duration_minutes INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);
