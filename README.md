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

## Run Tests

```bash
./test.sh
```

## CI/CD Pipeline

This project includes a comprehensive build pipeline using GitHub Actions:

### Pipeline Jobs

1. **Backend Tests** (`backend-test`)
   - Builds the Spring Boot application
   - Runs unit tests
   - Packages the JAR file

2. **Frontend Tests** (`frontend-test`)
   - Installs dependencies with pnpm
   - Runs ESLint checks
   - Performs TypeScript type checking
   - Builds the Next.js application

3. **Integration Tests** (`integration-test`)
   - Runs the full build script
   - Executes integration test suite
   - Validates end-to-end functionality

4. **Build Artifacts** (`build-artifacts`)
   - Creates production-ready artifacts
   - Uploads build outputs for deployment
   - Only runs on main branch

### Additional Pipelines

- **Docker Build** (`docker.yml`) - Builds and tests Docker images
- **Code Quality** (`quality.yml`) - Runs security scans and code analysis

### Pipeline Triggers

- **Push**: Triggers on `main` and `develop` branches
- **Pull Request**: Triggers on PRs to `main` branch
- **Manual**: Can be triggered manually from GitHub Actions

### Health Checks

```bash
./health-check.sh
```

This script validates:
- Build artifacts exist
- Services are healthy (if running)
- Application is ready for deployment
