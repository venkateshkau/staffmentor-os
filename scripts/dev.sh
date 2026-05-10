#!/usr/bin/env bash
set -euo pipefail

docker compose up -d
(cd backend && mvn spring-boot:run) &
(cd frontend && npm install && npm run dev)
