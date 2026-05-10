import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';

type ActionItem = {
    id: string;
    description: string;
    completed: boolean;
};

type VerificationDto = {
    id: string;
    submissionText: string;
    masteryLevel: string;
    confidenceScore: number;
    aiFeedback: string;
};

type CoachSession = {
    id: string;
    createdAt: string;
    todayFocus: string;
    actionItems: ActionItem[];
    calendarSuggestions: string[];
    followUpQuestion: string;
    motivation: string;
    completed: boolean;
    verification?: VerificationDto;
};

type DailyCoachRequest = {
    energyLevel: number;
    availableMinutes: number;
    completedYesterday: boolean;
    blockers: string;
    upcomingInterview: string;
    additionalNotes: string;
    workspacePath: string;
};

export default function DashboardPage() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    const [verificationText, setVerificationText] = useState('');
    const [form, setForm] = useState<DailyCoachRequest>({
        energyLevel: 7,
        availableMinutes: 90,
        completedYesterday: true,
        blockers: '',
        upcomingInterview: 'Canva',
        additionalNotes: '',
        workspacePath: ''
    });

    const todaySessionQuery = useQuery({
        queryKey: ['coach-today'],
        queryFn: async () => {
            const res = await api.get('/coach/today');
            return res.data ? (res.data as CoachSession) : null;
        }
    });

    const latestSessionQuery = useQuery({
        queryKey: ['coach-latest'],
        queryFn: async () => {
            const res = await api.get('/coach/latest');
            return res.data ? (res.data as CoachSession) : null;
        },
        enabled: !todaySessionQuery.data // Only fetch latest if today doesn't exist
    });

    const coachMutation = useMutation({
        mutationFn: async (payload: DailyCoachRequest) => {
            const res = await api.post<CoachSession>('/coach/daily', payload);
            return res.data;
        },
        onSuccess: (data) => {
            queryClient.setQueryData(['coach-today'], data);
        }
    });

    const toggleActionItem = useMutation({
        mutationFn: async ({ id, completed }: { id: string; completed: boolean }) => {
            await api.patch(`/coach/action-items/${id}/toggle?completed=${completed}`);
        },
        onMutate: async ({ id, completed }) => {
            // Optimistic update
            await queryClient.cancelQueries({ queryKey: ['coach-today'] });
            const previousSession = queryClient.getQueryData<CoachSession>(['coach-today']);

            if (previousSession) {
                queryClient.setQueryData<CoachSession>(['coach-today'], {
                    ...previousSession,
                    actionItems: previousSession.actionItems.map(item =>
                        item.id === id ? { ...item, completed } : item
                    )
                });
            }
            return { previousSession };
        },
        onError: (err, newTodo, context) => {
            if (context?.previousSession) {
                queryClient.setQueryData(['coach-today'], context.previousSession);
            }
        },
        onSettled: () => {
            queryClient.invalidateQueries({ queryKey: ['coach-today'] });
        }
    });

    const verifyMutation = useMutation({
        mutationFn: async (submissionText: string) => {
            const res = await api.post<VerificationDto>('/verification/submit', {
                sessionId: todaySessionQuery.data?.id,
                submissionText
            });
            return res.data;
        },
        onSuccess: (data) => {
            queryClient.setQueryData<CoachSession | undefined>(['coach-today'], old => {
                if (!old) return old;
                return { ...old, verification: data };
            });
        }
    });

    if (todaySessionQuery.isLoading) {
        return <p>Loading Dashboard...</p>;
    }

    const todaySession = todaySessionQuery.data;

    if (todaySession) {
        return (
            <>
                <h1>Today's Execution Plan</h1>
                
                <section className="card" style={{ borderLeft: '4px solid #4ade80' }}>
                    <h2>🎯 Focus</h2>
                    <p style={{ fontSize: '1.2rem', fontWeight: 500 }}>{todaySession.todayFocus}</p>
                </section>

                <section className="card">
                    <h2>✅ Action Items</h2>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        {todaySession.actionItems.map(item => (
                            <div key={item.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0.5rem', background: 'var(--surface-color)', borderRadius: '8px' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', fontSize: '1.1rem', flexGrow: 1, cursor: 'pointer' }}>
                                    <input 
                                        type="checkbox" 
                                        checked={item.completed}
                                        onChange={(e) => toggleActionItem.mutate({ id: item.id, completed: e.target.checked })}
                                        style={{ width: '1.5rem', height: '1.5rem', accentColor: '#4ade80' }}
                                    />
                                    <span style={{ textDecoration: item.completed ? 'line-through' : 'none', color: item.completed ? '#9ca3af' : 'inherit' }}>
                                        {item.description}
                                    </span>
                                </label>
                                {!item.completed && (
                                    <button 
                                        className="button outline" 
                                        style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                                        onClick={() => navigate(`/focus`, { state: { actionItem: item } })}
                                    >
                                        🎧 Focus Mode
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                </section>

                <section className="card">
                    <h2>📅 Calendar Suggestions</h2>
                    <ul style={{ listStyleType: 'none', padding: 0 }}>
                        {todaySession.calendarSuggestions.map((block, idx) => {
                            const eventTitle = `🧠 StaffMentor: ${block}`;
                            const eventDescription = `🎯 Today's Focus:
${todaySession.todayFocus}

✅ Action Items:
${todaySession.actionItems.map(item => `- ${item.description}`).join('\n')}

📚 Where to Look:
- Your local project workspace
- Official documentation for your stack
- Previous architectural decisions

🤖 Starting Prompt (Paste this into your AI pair programmer):
"Act as a Staff+ Engineering mentor. I am working on: '${todaySession.todayFocus}'. 
My specific tasks for this block are: 
${todaySession.actionItems.map(item => `- ${item.description}`).join('\n')}
Can you guide me step-by-step on the best approach, focusing on Staff+ qualities like reliability, maintainability, and clean architecture?"

🏆 Expected Outcome:
Complete the action items, gain confidence in the topic, update your Dashboard, and reflect on this progress tomorrow!`;

                            const gcalUrl = `https://calendar.google.com/calendar/render?action=TEMPLATE&text=${encodeURIComponent(eventTitle)}&details=${encodeURIComponent(eventDescription)}`;
                            return (
                                <li key={idx} style={{ marginBottom: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: 'var(--surface-color)', padding: '1rem', borderRadius: '8px' }}>
                                    <span style={{ fontWeight: 'bold' }}>{block}</span>
                                    <a href={gcalUrl} target="_blank" rel="noreferrer" className="button" style={{ textDecoration: 'none', fontSize: '0.9rem' }}>
                                        Add to Calendar
                                    </a>
                                </li>
                            );
                        })}
                    </ul>
                </section>

                {todaySession.verification ? (
                    <section className="card" style={{ borderLeft: '4px solid #8b5cf6' }}>
                        <h2>🎓 Knowledge Verified</h2>
                        <div style={{ background: 'var(--surface-color)', padding: '1rem', borderRadius: '8px', marginBottom: '1rem' }}>
                            <strong>Mastery Level:</strong> {todaySession.verification.masteryLevel} <br/>
                            <strong>Confidence:</strong> {todaySession.verification.confidenceScore}/10
                        </div>
                        <p style={{ fontStyle: 'italic' }}>{todaySession.verification.aiFeedback}</p>
                    </section>
                ) : todaySession.actionItems.every(i => i.completed) && todaySession.actionItems.length > 0 ? (
                    <section className="card form" style={{ borderLeft: '4px solid #3b82f6' }}>
                        <h2>🧠 Verify Knowledge</h2>
                        <p>Great job finishing your tasks! Summarize what you learned or paste your code snippet below for AI evaluation.</p>
                        <textarea 
                            className="textarea" 
                            placeholder="I implemented the concurrency handler by..."
                            value={verificationText}
                            onChange={(e) => setVerificationText(e.target.value)}
                            rows={4}
                        />
                        <button 
                            className="button" 
                            onClick={() => verifyMutation.mutate(verificationText)}
                            disabled={verifyMutation.isPending || !verificationText.trim()}
                        >
                            {verifyMutation.isPending ? 'Verifying...' : 'Submit Reflection'}
                        </button>
                    </section>
                ) : (
                    <div className="grid">
                        <section className="card">
                            <h2>🤔 Follow-Up Question</h2>
                            <p>{todaySession.followUpQuestion}</p>
                        </section>
                        <section className="card" style={{ background: 'linear-gradient(135deg, #1e3a8a, #3b82f6)' }}>
                            <h2 style={{ color: 'white' }}>🔥 Motivation</h2>
                            <p style={{ color: 'white', fontStyle: 'italic' }}>"{todaySession.motivation}"</p>
                        </section>
                    </div>
                )}
            </>
        );
    }

    // Otherwise, we are in check-in mode
    const latestSession = latestSessionQuery.data;

    const dueSnippetsQuery = useQuery({
        queryKey: ['snippets-due'],
        queryFn: async () => {
            const res = await api.get('/knowledge-snippets/due');
            return res.data as { id: string, content: string }[];
        }
    });

    const reviewSnippetMutation = useMutation({
        mutationFn: async ({ id, remembered }: { id: string, remembered: boolean }) => {
            await api.post(`/knowledge-snippets/${id}/review`, { remembered });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['snippets-due'] });
        }
    });

    return (
        <>
            <h1>Good Morning. Let's align for today.</h1>
            
            {latestSession && (
                <section className="card" style={{ borderLeft: '4px solid #6b7280', opacity: 0.8 }}>
                    <h2>Yesterday's Context</h2>
                    <p><strong>Focus:</strong> {latestSession.todayFocus}</p>
                    <ul style={{ margin: '0.5rem 0 0 1.5rem' }}>
                        {latestSession.actionItems.map(item => (
                            <li key={item.id}>
                                <span style={{ textDecoration: item.completed ? 'line-through' : 'none' }}>{item.description}</span>
                            </li>
                        ))}
                    </ul>
                </section>
            )}

            {dueSnippetsQuery.data && dueSnippetsQuery.data.length > 0 && (
                <section className="card" style={{ borderLeft: '4px solid #f59e0b', background: '#fffbeb' }}>
                    <h2 style={{ color: '#b45309' }}>🧠 Morning Review ({dueSnippetsQuery.data.length} due)</h2>
                    <p style={{ color: '#92400e' }}>Spaced repetition ensures you master these technical concepts.</p>
                    <div style={{ marginTop: '1rem', padding: '1rem', background: 'white', borderRadius: '8px', border: '1px solid #fcd34d' }}>
                        <p style={{ fontSize: '1.1rem', fontWeight: 500, marginBottom: '1rem' }}>{dueSnippetsQuery.data[0].content}</p>
                        <div style={{ display: 'flex', gap: '1rem' }}>
                            <button className="button" style={{ background: '#10b981' }} onClick={() => reviewSnippetMutation.mutate({ id: dueSnippetsQuery.data[0].id, remembered: true })}>
                                I Remembered
                            </button>
                            <button className="button outline" style={{ color: '#ef4444', borderColor: '#ef4444' }} onClick={() => reviewSnippetMutation.mutate({ id: dueSnippetsQuery.data[0].id, remembered: false })}>
                                Need to Review Again
                            </button>
                        </div>
                    </div>
                </section>
            )}

            <section className="card form">
                <h2>Daily Check-In</h2>
                
                <div>
                    <label>Energy Level (1-10)</label>
                    <input className="input" type="number" min={1} max={10} value={form.energyLevel} onChange={(e) => setForm({ ...form, energyLevel: Number(e.target.value) })} />
                </div>

                <div>
                    <label>Available Minutes Today</label>
                    <input className="input" type="number" min={15} value={form.availableMinutes} onChange={(e) => setForm({ ...form, availableMinutes: Number(e.target.value) })} />
                </div>

                <div>
                    <label>Completed Yesterday's Goals?</label>
                    <select className="select" value={String(form.completedYesterday)} onChange={(e) => setForm({ ...form, completedYesterday: e.target.value === 'true' })}>
                        <option value="true">Yes</option>
                        <option value="false">No</option>
                    </select>
                </div>

                <div>
                    <label>Blockers</label>
                    <textarea className="textarea" placeholder="What is slowing you down?" value={form.blockers} onChange={(e) => setForm({ ...form, blockers: e.target.value })} />
                </div>

                <div>
                    <label>Upcoming Interview Focus</label>
                    <input className="input" placeholder="e.g. Canva, System Design..." value={form.upcomingInterview} onChange={(e) => setForm({ ...form, upcomingInterview: e.target.value })} />
                </div>

                <div>
                    <label>Additional Notes</label>
                    <textarea className="textarea" placeholder="Anything else the coach should know?" value={form.additionalNotes} onChange={(e) => setForm({ ...form, additionalNotes: e.target.value })} />
                </div>

                <div>
                    <label>Local Workspace Context (RAG)</label>
                    <input className="input" placeholder="/Users/name/code/project" value={form.workspacePath} onChange={(e) => setForm({ ...form, workspacePath: e.target.value })} />
                    <p className="muted" style={{ fontSize: '0.8rem', marginTop: '0.2rem' }}>StaffMentor will scan recent files to give context-aware prompts.</p>
                </div>

                <button className="button" onClick={() => coachMutation.mutate(form)} disabled={coachMutation.isPending} style={{ marginTop: '1rem', fontSize: '1.1rem', padding: '1rem' }}>
                    {coachMutation.isPending ? 'Generating Coaching Plan...' : 'Generate Today\'s Plan'}
                </button>
            </section>
        </>
    );
}
