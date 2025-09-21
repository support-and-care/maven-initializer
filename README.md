# Maven Initializer

A full-stack application with Spring Boot backend and Next.js frontend.

## Prerequisites

- Java 25
- Node.js 18+
- pnpm

## How to Start the Project

### Quick Start

```bash
./start-dev.sh
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

## Build the Project

```bash
./build.sh
```
