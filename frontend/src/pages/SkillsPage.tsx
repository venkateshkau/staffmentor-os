import { FormEvent, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from '../api/client';
import { Skill } from '../types/domain';

export default function SkillsPage() {
  const queryClient = useQueryClient();
  const [name, setName] = useState('Java concurrency');
  const [category, setCategory] = useState('Java');

  const skills = useQuery({ queryKey: ['skills'], queryFn: async () => (await api.get<Skill[]>('/skills')).data });
  const createSkill = useMutation({
    mutationFn: async () => api.post('/skills', { name, category, currentLevel: 2, targetLevel: 5, confidenceScore: 2 }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['skills'] })
  });

  function submit(event: FormEvent) {
    event.preventDefault();
    createSkill.mutate();
  }

  return (
    <>
      <h1>Skills</h1>
      <form className="card form" onSubmit={submit}>
        <input className="input" value={name} onChange={e => setName(e.target.value)} placeholder="Skill name" />
        <input className="input" value={category} onChange={e => setCategory(e.target.value)} placeholder="Category" />
        <button className="button" type="submit">Create Skill</button>
      </form>
      <div className="grid">
        {skills.data?.map(skill => (
          <section className="card" key={skill.id}>
            <span className="badge">{skill.category}</span>
            <h2>{skill.name}</h2>
            <p>Current: {skill.currentLevel}/5 · Target: {skill.targetLevel}/5 · Confidence: {skill.confidenceScore}/5</p>
          </section>
        ))}
      </div>
    </>
  );
}
