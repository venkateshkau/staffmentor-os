import { FormEvent, useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { api } from '../api/client';

export default function CheckInPage() {
  const [studiedYesterday, setStudiedYesterday] = useState('Reviewed Java/Spring Boot and worked on StaffMentor OS.');
  const [availableMinutes, setAvailableMinutes] = useState(90);
  const [energyLevel, setEnergyLevel] = useState(4);
  const [blockers, setBlockers] = useState('Limited time after work.');
  const [upcomingInterviews, setUpcomingInterviews] = useState('Backend engineering interviews.');
  const [priorityGoal, setPriorityGoal] = useState('Become Staff+ Backend Engineer');

  const createCheckIn = useMutation({
    mutationFn: async () => api.post('/checkins', { studiedYesterday, availableMinutes, energyLevel, blockers, upcomingInterviews, priorityGoal })
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    createCheckIn.mutate();
  }

  return (
    <>
      <h1>Daily Check-in</h1>
      <form className="card form" onSubmit={submit}>
        <label>What did I study yesterday?</label>
        <textarea className="textarea" value={studiedYesterday} onChange={e => setStudiedYesterday(e.target.value)} />
        <label>Available minutes today</label>
        <input className="input" type="number" value={availableMinutes} onChange={e => setAvailableMinutes(Number(e.target.value))} />
        <label>Energy level: {energyLevel}/5</label>
        <input type="range" min="1" max="5" value={energyLevel} onChange={e => setEnergyLevel(Number(e.target.value))} />
        <label>Blockers</label>
        <textarea className="textarea" value={blockers} onChange={e => setBlockers(e.target.value)} />
        <label>Upcoming interviews</label>
        <textarea className="textarea" value={upcomingInterviews} onChange={e => setUpcomingInterviews(e.target.value)} />
        <label>Priority goal</label>
        <input className="input" value={priorityGoal} onChange={e => setPriorityGoal(e.target.value)} />
        <button className="button" type="submit">Save Check-in</button>
        {createCheckIn.isSuccess && <p>Check-in saved. Generate today’s study plan next.</p>}
      </form>
    </>
  );
}
