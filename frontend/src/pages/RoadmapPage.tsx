import { useQuery } from '@tanstack/react-query';
import { api } from '../api/client';

type Goal = {
    id: string;
    title: string;
    status: string;
    priority: number;
    targetDate: string | null;
    createdAt: string;
};

type Sprint = {
    id: string;
    title: string;
    status: string;
    targetDate: string;
    goals: { id: string; title: string; }[];
};

export default function RoadmapPage() {
    const goalsQuery = useQuery({
        queryKey: ['goals'],
        queryFn: async () => {
            const res = await api.get<Goal[]>('/goals');
            return res.data;
        }
    });

    const sprintsQuery = useQuery({
        queryKey: ['sprints'],
        queryFn: async () => {
            const res = await api.get<Sprint[]>('/sprints');
            return res.data;
        }
    });

    if (goalsQuery.isLoading || sprintsQuery.isLoading) {
        return <p>Loading Roadmap...</p>;
    }

    const goals = goalsQuery.data || [];
    const sprints = sprintsQuery.data || [];

    const activeGoals = goals.filter(g => g.status === 'ACTIVE').sort((a, b) => a.priority - b.priority);

    return (
        <>
            <h1>Gantt Roadmap & Timeline</h1>
            <p>Visualizing your current goals and adaptive interview sprints.</p>

            <section className="card">
                <h2>Active Interview Sprints</h2>
                {sprints.length === 0 ? (
                    <p className="muted">No active sprints. Use the Sprint Planner to create one.</p>
                ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        {sprints.map(sprint => (
                            <div key={sprint.id} style={{ padding: '1rem', background: 'var(--surface-color)', borderRadius: '8px', borderLeft: '4px solid var(--primary-color)' }}>
                                <h3>{sprint.title}</h3>
                                <p className="muted">Target: {sprint.targetDate}</p>
                                <div style={{ marginTop: '0.5rem', display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                                    {sprint.goals.map(g => (
                                        <span key={g.id} style={{ background: '#3b82f6', color: 'white', padding: '0.2rem 0.6rem', borderRadius: '4px', fontSize: '0.8rem' }}>
                                            {g.title}
                                        </span>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </section>

            <section className="card" style={{ marginTop: '2rem' }}>
                <h2>Active Goals Timeline</h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                    {activeGoals.map(goal => {
                        // Very simple visual representation:
                        // Width represents remaining time, color represents priority.
                        const isHighPriority = goal.priority === 1;
                        return (
                            <div key={goal.id} style={{ padding: '0.5rem', background: 'var(--surface-color)', borderRadius: '4px' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                                    <strong>{goal.title}</strong>
                                    <span className="muted" style={{ fontSize: '0.85rem' }}>Target: {goal.targetDate || 'No Date'}</span>
                                </div>
                                <div style={{ width: '100%', height: '8px', background: '#374151', borderRadius: '4px', overflow: 'hidden' }}>
                                    <div style={{ 
                                        width: '60%', // placeholder progress
                                        height: '100%', 
                                        background: isHighPriority ? '#ef4444' : '#10b981' 
                                    }} />
                                </div>
                            </div>
                        );
                    })}
                </div>
            </section>
        </>
    );
}
