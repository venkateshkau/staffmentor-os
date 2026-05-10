import { NavLink, Route, Routes, useLocation } from 'react-router-dom';
import DashboardPage from './pages/DashboardPage';
import GoalsPage from './pages/GoalsPage';
import SkillsPage from './pages/SkillsPage';
import StudyPlanPage from './pages/StudyPlanPage';
import SprintPlannerPage from './pages/SprintPlannerPage';
import RoadmapPage from './pages/RoadmapPage';
import FocusModePage from './pages/FocusModePage';
import StatsPage from './pages/StatsPage';

export default function App() {
  const location = useLocation();
  const isFocusMode = location.pathname === '/focus';

  if (isFocusMode) {
    return (
      <Routes>
        <Route path="/focus" element={<FocusModePage />} />
      </Routes>
    );
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <h2>StaffMentor OS</h2>
        <p className="muted">Career operating system for Staff+ backend growth.</p>
        <nav>
          <NavLink to="/">Dashboard</NavLink>
          <NavLink to="/sprints">Sprint Planner</NavLink>
          <NavLink to="/roadmap">Roadmap</NavLink>
          <NavLink to="/stats">Analytics</NavLink>
          <NavLink to="/goals">Goals</NavLink>
          <NavLink to="/skills">Skills</NavLink>
          <NavLink to="/study-plan">Study Plan</NavLink>
        </nav>
      </aside>
      <main className="content">
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/sprints" element={<SprintPlannerPage />} />
          <Route path="/roadmap" element={<RoadmapPage />} />
          <Route path="/stats" element={<StatsPage />} />
          <Route path="/goals" element={<GoalsPage />} />
          <Route path="/skills" element={<SkillsPage />} />
          <Route path="/study-plan" element={<StudyPlanPage />} />
        </Routes>
      </main>
    </div>
  );
}
