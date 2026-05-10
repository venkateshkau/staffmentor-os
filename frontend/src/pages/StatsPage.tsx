import { useQuery } from '@tanstack/react-query';
import { api } from '../api/client';

type DailyVelocity = {
    date: string;
    completionPercentage: number;
    focusedMinutes: number;
};

export default function StatsPage() {
    const statsQuery = useQuery({
        queryKey: ['stats-velocity'],
        queryFn: async () => {
            const res = await api.get<{ [key: string]: DailyVelocity }>('/stats/velocity');
            return res.data;
        }
    });

    if (statsQuery.isLoading) return <p>Loading Velocity Stats...</p>;

    const data = statsQuery.data || {};
    
    // Generate a simple array of the last 30 days
    const last30Days = Array.from({ length: 30 }).map((_, i) => {
        const d = new Date();
        d.setDate(d.getDate() - (29 - i));
        return d.toISOString().split('T')[0];
    });

    const getColor = (percentage?: number) => {
        if (percentage === undefined) return '#e5e7eb'; // light gray / no data
        if (percentage === 0) return '#fca5a5'; // red
        if (percentage < 50) return '#fcd34d'; // yellow
        if (percentage < 100) return '#34d399'; // light green
        return '#059669'; // dark green (100%)
    };

    return (
        <>
            <h1>Velocity & Habit Analytics</h1>
            <p>Track your daily consistency over time.</p>

            <section className="card">
                <h2>Last 30 Days</h2>
                <div style={{ display: 'flex', gap: '0.2rem', flexWrap: 'wrap', marginTop: '1rem' }}>
                    {last30Days.map(date => {
                        const stat = data[date];
                        return (
                            <div 
                                key={date} 
                                title={`${date}: ${stat ? stat.completionPercentage + '%' : 'No activity'}`}
                                style={{
                                    width: '20px', 
                                    height: '20px', 
                                    borderRadius: '4px',
                                    background: getColor(stat?.completionPercentage)
                                }}
                            />
                        );
                    })}
                </div>
                
                <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem', fontSize: '0.8rem' }}>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><div style={{ width: '12px', height: '12px', background: '#e5e7eb', borderRadius: '2px' }}/> No Activity</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><div style={{ width: '12px', height: '12px', background: '#fca5a5', borderRadius: '2px' }}/> 0%</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><div style={{ width: '12px', height: '12px', background: '#fcd34d', borderRadius: '2px' }}/> &lt;50%</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><div style={{ width: '12px', height: '12px', background: '#34d399', borderRadius: '2px' }}/> &lt;100%</span>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><div style={{ width: '12px', height: '12px', background: '#059669', borderRadius: '2px' }}/> 100%</span>
                </div>
            </section>
        </>
    );
}
