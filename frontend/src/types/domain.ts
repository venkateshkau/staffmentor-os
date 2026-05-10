export type Goal = {
  id: string;
  title: string;
  description?: string;
  status: 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'ARCHIVED';
  priority: number;
  targetDate?: string;
};

export type Skill = {
  id: string;
  name: string;
  category?: string;
  currentLevel: number;
  targetLevel: number;
  confidenceScore: number;
  lastPracticedDate?: string;
  notes?: string;
};

export type StudyPlan = {
  id: string;
  planDate: string;
  mainTopic: string;
  whyItMatters: string;
  studyTask: string;
  codingTask: string;
  staffReflectionQuestion: string;
  expectedOutput: string;
  suggestedCalendarBlock?: string;
  estimatedMinutes: number;
  aiModel?: string;
};

export type GoalForm = {
  title: string;
  description: string;
  status: string;
  priority: string;
};