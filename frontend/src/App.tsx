import { NavLink, Route, Routes } from 'react-router-dom';
import DashboardPage from './pages/DashboardPage';
import CoachPage from './pages/CoachPage';
import GoalsPage from './pages/GoalsPage';
import SkillsPage from './pages/SkillsPage';
import CheckInPage from './pages/CheckInPage';
import StudyPlanPage from './pages/StudyPlanPage';

export default function App() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <h2>StaffMentor OS</h2>
        <p className="muted">Career operating system for Staff+ backend growth.</p>
        <nav>
          <NavLink to="/">Dashboard</NavLink>
          <NavLink to="/coach">Coach</NavLink>
          <NavLink to="/goals">Goals</NavLink>
          <NavLink to="/skills">Skills</NavLink>
          <NavLink to="/check-in">Daily Check-in</NavLink>
          <NavLink to="/study-plan">Study Plan</NavLink>
        </nav>
      </aside>
      <main className="content">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/coach" element={<CoachPage />} />
          <Route path="/goals" element={<GoalsPage />} />
          <Route path="/skills" element={<SkillsPage />} />
          <Route path="/check-in" element={<CheckInPage />} />
          <Route path="/study-plan" element={<StudyPlanPage />} />
        </Routes>
      </main>
    </div>
  );
}
