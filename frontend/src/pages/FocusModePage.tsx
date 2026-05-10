import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { api } from '../api/client';

export default function FocusModePage() {
    const location = useLocation();
    const navigate = useNavigate();
    const actionItem = location.state?.actionItem;

    const [minutesLeft, setMinutesLeft] = useState(25);
    const [secondsLeft, setSecondsLeft] = useState(0);
    const [isActive, setIsActive] = useState(false);

    const logFocusSession = useMutation({
        mutationFn: async (duration: number) => {
            if (actionItem?.id) {
                await api.post('/focus-sessions', { actionItemId: actionItem.id, durationMinutes: duration });
            }
        }
    });

    useEffect(() => {
        if (!actionItem) {
            navigate('/');
        }
    }, [actionItem, navigate]);

    useEffect(() => {
        let interval: ReturnType<typeof setInterval> | null = null;
        if (isActive) {
            interval = setInterval(() => {
                if (secondsLeft === 0) {
                    if (minutesLeft === 0) {
                        clearInterval(interval!);
                        setIsActive(false);
                        logFocusSession.mutate(25); // Hardcoded to 25m for now
                        alert('Focus session complete!');
                    } else {
                        setMinutesLeft(minutesLeft - 1);
                        setSecondsLeft(59);
                    }
                } else {
                    setSecondsLeft(secondsLeft - 1);
                }
            }, 1000);
        } else if (!isActive && secondsLeft !== 0) {
            if (interval) clearInterval(interval);
        }
        return () => {
            if (interval) clearInterval(interval);
        };
    }, [isActive, secondsLeft, minutesLeft, logFocusSession]);

    const toggleTimer = () => setIsActive(!isActive);

    const resetTimer = () => {
        setIsActive(false);
        setMinutesLeft(25);
        setSecondsLeft(0);
    };

    if (!actionItem) return null;

    return (
        <div style={{ height: '100vh', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', background: 'var(--bg-color)' }}>
            <div className="card" style={{ maxWidth: '600px', width: '100%', textAlign: 'center', padding: '3rem' }}>
                <h1 style={{ fontSize: '1.5rem', color: 'var(--primary-color)', marginBottom: '1rem' }}>Deep Work Mode</h1>
                <p style={{ fontSize: '1.2rem', marginBottom: '2rem' }}>{actionItem.description}</p>
                
                <div style={{ fontSize: '5rem', fontWeight: 'bold', fontFamily: 'monospace', marginBottom: '2rem' }}>
                    {String(minutesLeft).padStart(2, '0')}:{String(secondsLeft).padStart(2, '0')}
                </div>

                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
                    <button className="button" onClick={toggleTimer} style={{ background: isActive ? '#ef4444' : '#10b981', minWidth: '120px' }}>
                        {isActive ? 'Pause' : 'Start Focus'}
                    </button>
                    <button className="button outline" onClick={resetTimer}>Reset</button>
                    <button className="button outline" onClick={() => navigate('/')}>Exit</button>
                </div>

                <div style={{ marginTop: '2rem' }}>
                    <p className="muted">Flow state audio will begin automatically when you press start.</p>
                    {isActive && (
                        <audio src="https://cdn.pixabay.com/download/audio/2022/02/07/audio_c6f2a2ba77.mp3?filename=ambient-piano-amp-strings-10711.mp3" autoPlay loop controls style={{ width: '100%', marginTop: '1rem' }} />
                    )}
                </div>
            </div>
        </div>
    );
}
