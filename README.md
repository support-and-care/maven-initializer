# Maven Initializer

Maven Initializer is a modern, user-friendly web application designed to simplify the process of 
creating new Maven-based Java projects. Its primary goal is to provide developers with a fast, 
intuitive, and reliable way to bootstrap new projects with all the necessary configurations, 
dependencies, and best practices, without the need to manually set up complex project structures.

## Prerequisites

- Java 21
- Node.js 22+
- pnpm

### For Docker Setup
- Docker
- Docker Compose

## How to Start the Project

### Docker Setup (Recommended)

1. **Create environment file**:
   ```bash
   # Create .env file in the root directory
   cat > .env << EOF
   NEXT_PUBLIC_BACKEND_URL=http://backend:8080
   EOF
   ```

2. **Build and start both services**:
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

- Frontend: http://localhost:4001
- Backend API: http://localhost:9081/api/hello

