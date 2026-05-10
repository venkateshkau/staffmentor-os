import { FormEvent, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from '../api/client';
import { Goal } from '../types/domain';

type GoalForm = {
    title: string;
    description: string;
    status: string;
    priority: string;
};

const priorityOptions = [
    { value: '1', label: 'P0 - Critical' },
    { value: '2', label: 'P1 - High' },
    { value: '3', label: 'P2 - Medium' },
    { value: '4', label: 'P3 - Low' },
    { value: '5', label: 'Backlog' }
];

const priorityMeta: Record<string, { title: string; color: string }> = {
    '1': { title: 'P0 - Critical', color: '#dc2626' },
    '2': { title: 'P1 - High Priority', color: '#ea580c' },
    '3': { title: 'P2 - Medium Priority', color: '#2563eb' },
    '4': { title: 'P3 - Low Priority', color: '#6b7280' },
    '5': { title: 'Backlog', color: '#7c3aed' }
};

const emptyEditForm: GoalForm = {
    title: '',
    description: '',
    status: 'ACTIVE',
    priority: '1'
};

const statusOptions = [
    { value: 'ACTIVE', label: 'Active' },
    { value: 'PAUSED', label: 'Paused' },
    { value: 'COMPLETED', label: 'Completed' },
    { value: 'ARCHIVED', label: 'Archived' }
];

export default function GoalsPage() {
    const queryClient = useQueryClient();

    const [title, setTitle] = useState('Become Staff+ Backend Engineer');
    const [description, setDescription] = useState(
        'Build systems thinking, AI engineering, architecture, and interview readiness.'
    );
    const [priority, setPriority] = useState('1');

    const [editingGoalId, setEditingGoalId] = useState<string | null>(null);
    const [editForm, setEditForm] = useState<GoalForm>(emptyEditForm);

    const goals = useQuery({
        queryKey: ['goals'],
        queryFn: async () => (await api.get<Goal[]>('/goals')).data
    });

    const createGoal = useMutation({
        mutationFn: async () =>
            api.post('/goals', {
                title,
                description,
                status: 'ACTIVE',
                priority
            }),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['goals'] });
            setTitle('');
            setDescription('');
            setPriority('1');
        }
    });

    const updateGoal = useMutation({
        mutationFn: async ({ id, payload }: { id: string; payload: GoalForm }) =>
            api.put(`/goals/${id}`, payload),
        onSuccess: async () => {
            setEditingGoalId(null);
            setEditForm(emptyEditForm);
            await queryClient.invalidateQueries({ queryKey: ['goals'] });
        }
    });

    const archiveGoal = useMutation({
        mutationFn: async (id: string) => api.patch(`/goals/${id}/archive`),
        onSuccess: async () => {
            await queryClient.invalidateQueries({ queryKey: ['goals'] });
        }
    });

    const visibleGoals = goals.data?.filter((goal) => goal.status !== 'ARCHIVED') ?? [];

    function submit(event: FormEvent) {
        event.preventDefault();

        if (!title.trim()) return;

        createGoal.mutate();
    }

    function startEditing(goal: Goal) {
        setEditingGoalId(goal.id);
        setEditForm({
            title: goal.title ?? '',
            description: goal.description ?? '',
            status: goal.status ?? 'ACTIVE',
            priority: String(goal.priority ?? '1')
        });
    }

    function cancelEditing() {
        setEditingGoalId(null);
        setEditForm(emptyEditForm);
    }

    function saveEditing(goalId: string) {
        if (!editForm.title.trim()) return;

        updateGoal.mutate({
            id: goalId,
            payload: {
                ...editForm,
                title: editForm.title.trim(),
                description: editForm.description.trim()
            }
        });
    }

    const groupedGoals =
        visibleGoals.reduce<Record<string, Goal[]>>((acc, goal) => {
            const key = String(goal.priority ?? '4');

            if (!acc[key]) {
                acc[key] = [];
            }

            acc[key].push(goal);

            return acc;
        }, {}) ?? {};

    return (
        <>
            <h1>Goals</h1>

            <form className="card form" onSubmit={submit}>
                <select className={`select priority-${priority}`} value={priority} onChange={(e) => setPriority(e.target.value)}>
                    {priorityOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>

                <input
                    className="input"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="Goal title"
                />

                <textarea
                    className="textarea"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    placeholder="Description"
                />

                <button className="button" type="submit" disabled={createGoal.isPending || !title.trim()}>
                    {createGoal.isPending ? 'Creating...' : 'Create Goal'}
                </button>
            </form>

            {goals.isLoading && <p className="muted">Loading goals...</p>}

            {goals.isError && (
                <section className="card">
                    <p>Unable to load goals. Check if backend is running.</p>
                </section>
            )}

            {Object.entries(priorityMeta).map(([priorityKey, meta]) => {
                const priorityGoals = groupedGoals[priorityKey];

                if (!priorityGoals?.length) {
                    return null;
                }

                return (
                    <section key={priorityKey} className="priority-section">
                        <div className="priority-header">
                            <div className="priority-dot" style={{ background: meta.color }} />
                            <h2>{meta.title}</h2>
                            <span className="badge">{priorityGoals.length}</span>
                        </div>

                        <div className="priority-grid">
                            {priorityGoals.map((goal) => {
                                const isEditing = editingGoalId === goal.id;
                                const goalPriority = String(goal.priority ?? '4');

                                return (
                                    <section className={`card goal-card priority-${goalPriority}`} key={goal.id}>
                                        {isEditing ? (
                                            <div className="form">
                                                <div>
                          <span className={`badge priority-badge-${editForm.priority}`}>
                            Editing · {priorityMeta[editForm.priority]?.title ?? 'Priority'}
                          </span><span className={`badge status-${goal.status.toLowerCase()}`}>{goal.status}</span>
                                                </div>

                                                <select
                                                    className={`select priority-${priority}`}
                                                    value={editForm.priority}
                                                    onChange={(e) => setEditForm({
                                                        ...editForm,
                                                        priority: e.target.value
                                                    })}
                                                >
                                                    {priorityOptions.map((option) => (
                                                        <option key={option.value} value={option.value}>
                                                            {option.label}
                                                        </option>
                                                    ))}
                                                </select>

                                                <input
                                                    className="input"
                                                    value={editForm.title}
                                                    onChange={(e) => setEditForm({...editForm, title: e.target.value})}
                                                    placeholder="Goal title"
                                                    autoFocus
                                                />

                                                <textarea
                                                    className="textarea"
                                                    value={editForm.description}
                                                    onChange={(e) => setEditForm({
                                                        ...editForm,
                                                        description: e.target.value
                                                    })}
                                                    placeholder="Description"
                                                />
                                                <select
                                                    className="select"
                                                    value={editForm.status}
                                                    onChange={(e) => setEditForm({...editForm, status: e.target.value})}
                                                >
                                                    {statusOptions.map((option) => (
                                                        <option key={option.value} value={option.value}>
                                                            {option.label}
                                                        </option>
                                                    ))}
                                                </select>

                                                <div className="goal-actions">
                                                    <button
                                                        className="button"
                                                        type="button"
                                                        onClick={() => saveEditing(goal.id)}
                                                        disabled={updateGoal.isPending || !editForm.title.trim()}
                                                    >
                                                        {updateGoal.isPending ? 'Saving...' : 'Save'}
                                                    </button>
                                                    <button
                                                        className="button secondary"
                                                        type="button"
                                                        onClick={() => archiveGoal.mutate(goal.id)}
                                                        disabled={archiveGoal.isPending}
                                                    >
                                                        Archive
                                                    </button>
                                                    <button className="button secondary" type="button"
                                                            onClick={cancelEditing}>
                                                        Cancel
                                                    </button>
                                                </div>
                                            </div>
                                        ) : (
                                            <>
                        <span className={`badge priority-badge-${goalPriority}`}>
                          {goal.status} · {priorityMeta[goalPriority]?.title ?? `P${goalPriority}`}
                        </span>

                                                <h2>{goal.title}</h2>
                                                <p>{goal.description}</p>

                                                <div className="goal-actions">
                                                    <button className="button secondary" type="button" onClick={() => startEditing(goal)}>
                                                        Edit
                                                    </button>
                                                </div>
                                            </>
                                        )}
                                    </section>
                                );
                            })}
                        </div>
                    </section>
                );
            })}
        </>
    );
}