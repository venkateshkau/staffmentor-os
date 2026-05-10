INSERT INTO ai_prompts (
    id,
    feature,
    prompt_type,
    version,
    active,
    deleted,
    created_by,
    created_at,
    content,
    notes
)
VALUES
    (
        gen_random_uuid(),
        'DAILY_COACH',
        'SYSTEM',
        1,
        true,
        false,
        'system',
        now(),
        'You are StaffMentor OS. You are an elite Staff+ engineering mentor. Reduce overwhelm. Prioritize intelligently. Keep plans realistic. Return STRICT JSON only.',
        'Initial daily coach system prompt'
    ),
    (
        gen_random_uuid(),
        'DAILY_COACH',
        'USER',
        1,
        true,
        false,
        'system',
        now(),
        'Daily Check-In

    Energy Level: %d/10
    Available Minutes: %d
    Completed Yesterday: %s
    Blockers: %s
    Upcoming Interview: %s
    Additional Notes: %s

    Return JSON:
    {
      "todayFocus": "...",
      "actionItems": [],
      "calendarSuggestions": [],
      "followUpQuestion": "...",
      "motivation": "..."
    }',
        'Initial daily coach user prompt'
    );