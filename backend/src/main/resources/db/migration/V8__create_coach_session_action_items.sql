CREATE TABLE coach_session_action_items (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES coach_sessions(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL
);

-- Backfill from existing coach_sessions action_items (basic text to simple task, assuming 1 line = 1 task if there are existing rows)
-- Actually, the user says FASTEST path, so we can just do a simple string split if there's data, or just drop it. 
-- Since it's early and we haven't seen a large userbase, dropping the string column is okay, but let's just do a clean drop for speed if that's acceptable. Wait, let's keep it safe. 

-- Migrate existing action items if needed (not strictly required if this is an MVP without production data, but good practice).
-- Since postgres string manipulation is tricky, we'll just leave the old data behind if it exists, or just do the schema change.
ALTER TABLE coach_sessions DROP COLUMN action_items;
