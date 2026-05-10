import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from '../api/client';
import { StudyPlan } from '../types/domain';

export default function StudyPlanPage() {
  const queryClient = useQueryClient();
  const today = useQuery({
    queryKey: ['study-plan-today'],
    queryFn: async () => (await api.get<StudyPlan>('/study-plans/today')).data,
    retry: false
  });
  const generate = useMutation({
    mutationFn: async () => (await api.post<StudyPlan>('/study-plans/generate')).data,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['study-plan-today'] })
  });

  const plan = generate.data ?? today.data;

  return (
    <>
      <h1>Today’s Study Plan</h1>
      <section className="card">
        <button className="button" onClick={() => generate.mutate()} disabled={generate.isPending}>
          {generate.isPending ? 'Generating...' : 'Generate AI Study Plan'}
        </button>
        <p className="muted">Requires a daily check-in. If OPENAI_API_KEY is not configured, backend returns a safe local fallback plan.</p>
      </section>
      {plan && (
        <section className="card">
          <span className="badge">{plan.estimatedMinutes} min · {plan.aiModel}</span>
          <h2>{plan.mainTopic}</h2>
          <h3>Why it matters</h3>
          <p>{plan.whyItMatters}</p>
          <h3>Study task</h3>
          <p>{plan.studyTask}</p>
          <h3>Coding task</h3>
          <p>{plan.codingTask}</p>
          <h3>Staff+ reflection</h3>
          <p>{plan.staffReflectionQuestion}</p>
          <h3>Expected output</h3>
          <p>{plan.expectedOutput}</p>
          <h3>Calendar suggestion</h3>
          <p>{plan.suggestedCalendarBlock}</p>
        </section>
      )}
      {today.isError && !plan && <section className="card"><p>No plan yet. Create a check-in and generate one.</p></section>}
    </>
  );
}
