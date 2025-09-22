# Maven Initializer

A full-stack application with Spring Boot backend and Next.js frontend.

## Prerequisites

### For Manual Setup
- Java 25
- Node.js 18+
- pnpm

### For Docker Setup
- Docker
- Docker Compose

## How to Start the Project

### Docker Setup (Recommended)

```bash
# Build and start both services
docker compose up --build

# Or run in detached mode
docker compose up --build -d
```

### Manual Setup

1. **Start the backend** (in terminal 1):

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **Start the frontend** (in terminal 2):
   ```bash
   cd frontend
   pnpm install
   pnpm dev
   ```

### Access the Application

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/hello

