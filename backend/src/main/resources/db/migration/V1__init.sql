CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE goals (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(40) NOT NULL,
    priority INTEGER NOT NULL DEFAULT 3,
    target_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE skills (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    category VARCHAR(120),
    current_level INTEGER NOT NULL,
    target_level INTEGER NOT NULL,
    confidence_score INTEGER NOT NULL,
    last_practiced_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE daily_checkins (
    id UUID PRIMARY KEY,
    checkin_date DATE NOT NULL UNIQUE,
    studied_yesterday TEXT,
    available_minutes INTEGER NOT NULL,
    energy_level INTEGER NOT NULL,
    blockers TEXT,
    upcoming_interviews TEXT,
    priority_goal TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE study_plans (
    id UUID PRIMARY KEY,
    plan_date DATE NOT NULL,
    checkin_id UUID REFERENCES daily_checkins(id),
    main_topic VARCHAR(255) NOT NULL,
    why_it_matters TEXT NOT NULL,
    study_task TEXT NOT NULL,
    coding_task TEXT NOT NULL,
    staff_reflection_question TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    suggested_calendar_block VARCHAR(120),
    estimated_minutes INTEGER NOT NULL,
    ai_model VARCHAR(120),
    raw_ai_response TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_skills_name ON skills(name);
CREATE INDEX idx_study_plans_plan_date ON study_plans(plan_date);
