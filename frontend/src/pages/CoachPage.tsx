import { useState } from 'react';
import {useMutation, useQuery} from '@tanstack/react-query';
import { api } from '../api/client';

type DailyCoachRequest = {
    energyLevel: number;
    availableMinutes: number;
    completedYesterday: boolean;
    blockers: string;
    upcomingInterview: string;
    additionalNotes: string;
};

type DailyCoachResponse = {
    todayFocus: string;
    actionItems: string[];
    calendarSuggestions: string[];
    followUpQuestion: string;
    motivation: string;
};

export default function CoachPage() {
    const [form, setForm] = useState<DailyCoachRequest>({
        energyLevel: 7,
        availableMinutes: 90,
        completedYesterday: true,
        blockers: '',
        upcomingInterview: 'Canva',
        additionalNotes: ''
    });

    const coachMutation = useMutation({
        mutationFn: async (payload: DailyCoachRequest) => {
            const response = await api.post<DailyCoachResponse>(
                '/coach/daily',
                payload
            );

            return response.data;
        }
    });

    const latestSessionQuery = useQuery({
        queryKey: ['latest-coach-session'],
        queryFn: async () => {
            const response = await api.get('/coach/latest');
            return response.data;
        }
    });

    function submit() {
        coachMutation.mutate(form);
    }

    return (
        <>
            {latestSessionQuery.data && (
                <section className="card">
                    <h2>Yesterday's Coaching Plan</h2>

                    <p>
                        <strong>Focus:</strong>{' '}
                        {latestSessionQuery.data.todayFocus}
                    </p>

                    <h3>Action Items</h3>

                    <ul>
                        {latestSessionQuery.data.actionItems.map(
                            (item: string, index: number) => (
                                <li key={index}>{item}</li>
                            )
                        )}
                    </ul>

                    <p>
                        <strong>Follow-Up Question:</strong>{' '}
                        {latestSessionQuery.data.followUpQuestion}
                    </p>
                </section>
            )}

            <h1>Daily Coach</h1>

            <section className="card form">
                <div>
                    <label>Energy Level</label>

                    <input
                        className="input"
                        type="number"
                        min={1}
                        max={10}
                        value={form.energyLevel}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                energyLevel: Number(e.target.value)
                            })
                        }
                    />
                </div>

                <div>
                    <label>Available Minutes</label>

                    <input
                        className="input"
                        type="number"
                        min={15}
                        value={form.availableMinutes}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                availableMinutes: Number(e.target.value)
                            })
                        }
                    />
                </div>

                <div>
                    <label>Completed Yesterday?</label>

                    <select
                        className="select"
                        value={String(form.completedYesterday)}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                completedYesterday: e.target.value === 'true'
                            })
                        }
                    >
                        <option value="true">Yes</option>
                        <option value="false">No</option>
                    </select>
                </div>

                <div>
                    <label>Blockers</label>

                    <textarea
                        className="textarea"
                        placeholder="What is slowing you down?"
                        value={form.blockers}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                blockers: e.target.value
                            })
                        }
                    />
                </div>

                <div>
                    <label>Upcoming Interview</label>

                    <input
                        className="input"
                        placeholder="Canva, Atlassian..."
                        value={form.upcomingInterview}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                upcomingInterview: e.target.value
                            })
                        }
                    />
                </div>

                <div>
                    <label>Additional Notes</label>

                    <textarea
                        className="textarea"
                        placeholder="Anything else the coach should know?"
                        value={form.additionalNotes}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                additionalNotes: e.target.value
                            })
                        }
                    />
                </div>

                <button
                    className="button"
                    onClick={submit}
                    disabled={coachMutation.isPending}
                >
                    {coachMutation.isPending
                        ? 'Generating Coaching Plan...'
                        : 'Generate Daily Coaching'}
                </button>
            </section>

            {coachMutation.data && (
                <>
                    <section className="card">
                        <h2>Today's Focus</h2>
                        <p>{coachMutation.data.todayFocus}</p>
                    </section>

                    <section className="card">
                        <h2>Action Items</h2>

                        <ul>
                            {coachMutation.data.actionItems.map((item, index) => (
                                <li key={index}>{item}</li>
                            ))}
                        </ul>
                    </section>

                    <section className="card">
                        <h2>Calendar Suggestions</h2>

                        <ul>
                            {coachMutation.data.calendarSuggestions.map(
                                (item, index) => (
                                    <li key={index}>{item}</li>
                                )
                            )}
                        </ul>
                    </section>

                    <section className="card">
                        <h2>Follow-Up Question</h2>
                        <p>{coachMutation.data.followUpQuestion}</p>
                    </section>

                    <section className="card">
                        <h2>Motivation</h2>
                        <p>{coachMutation.data.motivation}</p>
                    </section>
                </>
            )}

            {coachMutation.isError && (
                <section className="card">
                    <p>Failed to generate coaching response.</p>
                </section>
            )}
        </>
    );
}