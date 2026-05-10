# StaffMentor OS

StaffMentor OS is an AI-first, local-data-controlled career operating system for backend engineers. It helps track goals, skills, daily check-ins, study plans, job applications, mentor conversations, project ideas, interview preparation, and long-term Staff+ engineering growth.

This repository is intentionally designed as a **portfolio-grade modular monorepo** using Java 17, Spring Boot 3, React, PostgreSQL, Flyway, and OpenAI integration.

---

## Why this project exists

The goal is not to build a toy chatbot. The goal is to build a serious backend + AI engineering system that demonstrates:

- modular monolith architecture
- Java 17 and Spring Boot backend engineering
- PostgreSQL persistence and schema migrations
- AI orchestration through a backend service layer
- strict JSON AI responses
- local-first career data
- future RAG with pgvector
- future MCP/tool integrations
- product thinking and Staff+ engineering habits

---

## Current Vertical Slice

This starter repo includes the first working vertical slice:

1. Goal CRUD
2. Skill CRUD
3. Daily check-in
4. AI-generated study plan
5. Persisted study plans
6. React frontend pages
7. PostgreSQL via Docker Compose
8. Flyway schema migration
9. OpenAI adapter with local fallback when no API key is configured

---

## Tech Stack

### Backend

- Java 17 LTS
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Jakarta Validation
- PostgreSQL
- Flyway
- Lombok
- OpenAI Responses API integration

### Frontend

- React
- Vite
- TypeScript
- TanStack Query
- Axios
- CSS starter styling

### Infrastructure

- Docker Compose
- PostgreSQL 16

---

## Repository Structure

```text
staffmentor-os/
├── backend/
│   └── src/main/java/com/staffmentor/
│       ├── ai/
│       ├── checkin/
│       ├── common/
│       ├── goal/
│       ├── skill/
│       └── study/
├── frontend/
│   └── src/
│       ├── api/
│       ├── pages/
│       ├── types/
│       └── components/
├── prompts/
├── scripts/
├── docs/
├── infra/
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## High-Level Architecture

```text
React Frontend
    ↓
Spring Boot REST API
    ↓
Application Services
    ↓
Domain Modules
    ↓
PostgreSQL

AI Flow:
StudyPlanController
    ↓
StudyPlanService
    ↓
StudyPlanOrchestrator
    ↓
StudyPlanPromptBuilder
    ↓
OpenAiClient
    ↓
Strict JSON response
    ↓
Persist StudyPlan
```

---

## Backend Module Design

```text
com.staffmentor
├── common
│   ├── advice
│   ├── config
│   ├── exception
│   └── response
├── goal
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
├── skill
├── checkin
├── study
└── ai
    ├── client
    ├── dto
    ├── orchestrator
    └── prompt
```

The AI layer is intentionally separate from controllers. This prevents tight coupling and makes it easier to add RAG, caching, prompt versioning, model routing, and MCP tools later.

---

## Prerequisites

Install:

- Java 17
- Maven 3.9+
- Node.js 20+ or 22 LTS
- Docker Desktop

Check versions:

```bash
java -version
mvn -version
node -v
npm -v
docker --version
```

---

## Local Setup

### 1. Clone and enter repo

```bash
git clone <your-repo-url>
cd staffmentor-os
```

### 2. Start PostgreSQL

```bash
docker compose up -d
```

### 3. Optional: configure OpenAI

Create `.env` from example:

```bash
cp .env.example .env
```

Export your API key before starting backend:

```bash
export OPENAI_API_KEY="your_api_key_here"
export OPENAI_MODEL="gpt-4.1-mini"
```

If you do not set `OPENAI_API_KEY`, the backend returns a useful fallback study plan. This is intentional for local reliability.

### 4. Run backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

### 5. Run frontend

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```text
http://localhost:5173
```

---

## API Endpoints

### Goals

```http
POST   /api/goals
GET    /api/goals
GET    /api/goals/{id}
PUT    /api/goals/{id}
DELETE /api/goals/{id}
```

### Skills

```http
POST /api/skills
GET  /api/skills
PUT  /api/skills/{id}
```

### Daily Check-in

```http
POST /api/checkins
GET  /api/checkins/latest
```

### Study Plans

```http
POST /api/study-plans/generate
GET  /api/study-plans/today
```

---

## Example API Usage

### Create Goal

```bash
curl -X POST http://localhost:8080/api/goals \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Become Staff+ Backend Engineer",
    "description": "Improve architecture, AI engineering, system design, Java, and leadership depth.",
    "status": "ACTIVE",
    "priority": 1
  }'
```

### Create Skill

```bash
curl -X POST http://localhost:8080/api/skills \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Java concurrency",
    "category": "Java",
    "currentLevel": 2,
    "targetLevel": 5,
    "confidenceScore": 2
  }'
```

### Create Daily Check-in

```bash
curl -X POST http://localhost:8080/api/checkins \
  -H "Content-Type: application/json" \
  -d '{
    "studiedYesterday": "Reviewed Spring Boot and Java concurrency.",
    "availableMinutes": 90,
    "energyLevel": 4,
    "blockers": "Limited time after work.",
    "upcomingInterviews": "Backend interviews.",
    "priorityGoal": "Become Staff+ Backend Engineer"
  }'
```

### Generate Study Plan

```bash
curl -X POST http://localhost:8080/api/study-plans/generate
```

---

## AI Design Decisions

### 1. OpenAI is called only from backend

The frontend never receives the OpenAI API key.

### 2. Strict JSON output

The study planner asks AI to return strict JSON fields:

```json
{
  "mainTopic": "...",
  "whyItMatters": "...",
  "studyTask": "...",
  "codingTask": "...",
  "staffReflectionQuestion": "...",
  "expectedOutput": "...",
  "suggestedCalendarBlock": "...",
  "estimatedMinutes": 90
}
```

### 3. Graceful fallback

If `OPENAI_API_KEY` is missing, the system still works with a local fallback plan. This is important for reliability and demos.

### 4. AI orchestration is separated

```text
StudyPlanService
    → StudyPlanOrchestrator
        → PromptBuilder
        → AiClient
```

This makes it easier to add:

- model routing
- prompt versioning
- token usage tracking
- response caching
- RAG context assembly
- eval tests
- local model fallback

---

## Future RAG Design

Later, add:

```text
PostgreSQL + pgvector
```

Knowledge sources:

- Obsidian notes
- resume versions
- job descriptions
- project logs
- interview notes
- code review notes
- study history

RAG flow:

```text
Import document
    ↓
Chunk document
    ↓
Generate embedding
    ↓
Store chunk + vector
    ↓
Retrieve relevant chunks
    ↓
Inject into mentor prompt
```

Proposed package:

```text
com.staffmentor.rag
├── DocumentIngestionService
├── ChunkingService
├── EmbeddingService
├── RetrievalService
└── RagContextAssembler
```

---

## Future MCP Design

MCP is useful later when StaffMentor OS needs tool access:

- Google Calendar
- Gmail
- GitHub
- local files
- Obsidian
- job tracking tools

Example future flow:

```text
User: Create study block tomorrow at 7 PM
    ↓
Mentor Engine decides tool action
    ↓
MCP Calendar Tool
    ↓
Google Calendar event created after approval
```

Important rule:

> Human approval is required before external side effects such as sending email or applying for jobs.

---

## Roadmap

### Phase 1 — Foundation

- Docker Compose
- Spring Boot backend
- React frontend
- Goal CRUD
- Skill CRUD
- Daily check-in
- AI-generated study plan

### Phase 2 — Mentor Engine

- Mentor modes
- Code coach
- System design mentor
- Interview coach
- Project mentor

### Phase 3 — Job Tracker

- Job applications
- Status tracking
- Resume version links
- Cover letter drafts
- Next action reminders

### Phase 4 — Weekly Review

- Weekly progress review
- Skill gap analysis
- Missed study sessions
- Next week plan

### Phase 5 — RAG

- pgvector
- document ingestion
- local knowledge retrieval

### Phase 6 — Tool Integrations

- Google Calendar
- Gmail drafts
- GitHub activity
- MCP tool layer

---

## Engineering Principles

- Build vertical slices
- Keep AI orchestration isolated
- Persist important AI outputs
- Prefer DTOs over exposing entities
- Use Flyway for schema evolution
- Keep local development simple
- Avoid premature microservices
- Add tests as modules stabilize
- Treat prompts as versioned product assets
- Design for reliability even when AI fails

---

## Portfolio Talking Points

This project demonstrates:

- backend architecture
- Java 17 / Spring Boot engineering
- modular monolith design
- AI product integration
- prompt engineering
- strict AI output parsing
- graceful fallback design
- database modeling
- schema migration
- React frontend implementation
- roadmap toward RAG and MCP
- Staff+ engineering mindset

---

## First Demo Script

1. Open the frontend.
2. Create goal: `Become Staff+ Backend Engineer`.
3. Create skill: `Java concurrency`.
4. Submit daily check-in.
5. Generate AI study plan.
6. Explain the backend architecture:

```text
Controller → Service → Orchestrator → Prompt Builder → AI Client → Parser → Persistence
```

That is already a strong first portfolio milestone.
