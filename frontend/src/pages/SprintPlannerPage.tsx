import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { api } from '../api/client';

type SprintResponse = {
    id: string;
    title: string;
    targetDate: string;
    status: string;
    goals: {
        id: string;
        title: string;
        status: string;
        priority: number;
    }[];
};

export default function SprintPlannerPage() {
    const [title, setTitle] = useState('');
    const [jdText, setJdText] = useState('');

    const sprintMutation = useMutation({
        mutationFn: async () => {
            const res = await api.post<SprintResponse>('/sprints/analyze-jd', { title, jdText });
            return res.data;
        }
    });

    return (
        <>
            <h1>Adaptive Sprint Planner</h1>
            <p>Paste a Job Description (JD) below. The AI mentor will automatically extract required skills, compare them against your active goals, pause irrelevant goals, and create a targeted interview sprint.</p>

            {!sprintMutation.data && (
                <section className="card form">
                    <div>
                        <label>Sprint Title</label>
                        <input 
                            className="input" 
                            placeholder="e.g. Senior Backend Engineer - Stripe" 
                            value={title} 
                            onChange={(e) => setTitle(e.target.value)} 
                        />
                    </div>
                    
                    <div>
                        <label>Job Description</label>
                        <textarea 
                            className="textarea" 
                            rows={12} 
                            placeholder="Paste the full JD here..." 
                            value={jdText} 
                            onChange={(e) => setJdText(e.target.value)} 
                        />
                    </div>

                    <button 
                        className="button" 
                        onClick={() => sprintMutation.mutate()} 
                        disabled={sprintMutation.isPending || !jdText.trim()}
                        style={{ marginTop: '1rem', background: 'var(--primary-color)' }}
                    >
                        {sprintMutation.isPending ? 'Analyzing JD & Reprioritizing Goals...' : 'Generate Interview Sprint'}
                    </button>
                </section>
            )}

            {sprintMutation.data && (
                <section className="card" style={{ borderLeft: '4px solid var(--primary-color)' }}>
                    <h2>Sprint Created: {sprintMutation.data.title}</h2>
                    <p><strong>Target Date:</strong> {sprintMutation.data.targetDate}</p>

                    <h3>New High-Priority Goals</h3>
                    <ul style={{ listStyleType: 'none', padding: 0 }}>
                        {sprintMutation.data.goals.map(g => (
                            <li key={g.id} style={{ padding: '0.75rem', background: 'var(--surface-color)', marginBottom: '0.5rem', borderRadius: '4px', borderLeft: '4px solid #ef4444' }}>
                                <strong>{g.title}</strong> (Priority: {g.priority})
                            </li>
                        ))}
                    </ul>

                    <p style={{ marginTop: '1rem', fontStyle: 'italic' }}>
                        Your other active goals that were not relevant to this JD have been automatically paused. You can view them on the Goals page or the Roadmap Timeline.
                    </p>

                    <button className="button" onClick={() => sprintMutation.reset()} style={{ marginTop: '1rem' }}>
                        Plan Another Sprint
                    </button>
                </section>
            )}
        </>
    );
}
