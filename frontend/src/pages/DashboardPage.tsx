export default function DashboardPage() {
  return (
    <>
      <h1>StaffMentor OS</h1>
      <div className="grid">
        <section className="card">
          <h2>Today</h2>
          <p>Create a daily check-in, then generate an AI-powered study plan.</p>
        </section>
        <section className="card">
          <h2>Staff+ Focus</h2>
          <p>Every plan should include reliability, scalability, observability, security, cost, maintainability, and operability thinking.</p>
        </section>
        <section className="card">
          <h2>Next Milestones</h2>
          <p>Job tracker, mentor modes, RAG with pgvector, and Calendar/Gmail integrations.</p>
        </section>
      </div>
    </>
  );
}
