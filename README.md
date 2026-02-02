# Apache Maven™ Initializer

Apache Maven™ Initializer is a modern, user-friendly web application designed to simplify the process of creating new Apache Maven™ projects.
Its primary goal is to provide developers with a fast, intuitive, and reliable way to bootstrap new projects with all the necessary configurations, dependencies, and best practices, without the need to manually set up complex project structures.

## Prerequisites

- Java 25
- Node.js 22+
- pnpm

### For Docker Setup

- Docker
- Docker Compose

## Version Management Tools

This project includes configuration files for version management:

- **`backend/.sdkmanrc`**: Java 25 (use `sdk env install` to install & activate)
- **`frontend/.nvmrc`**: Node.js v22.19.0 (use `nvm install` to install & activate)

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

- Frontend: http://localhost:4001
- Backend API: http://localhost:9081/api/hello

## Monitoring

### Prometheus

- Metrics endpoint: `/api/actuator/prometheus`
- Exposes application metrics for monitoring

### Loki

- Logs are automatically sent to: `https://loki.open-elements.cloud`
- App name: `maven-initializer-backend`
- Query in Grafana: `{app="maven-initializer-backend"}`

## How to contribute

We welcome contributions from the community!

If you'd like to give feedback, please open an issue on our [GitHub discussion](https://github.com/support-and-care/maven-initializer/discussions).

If you'd like to contribute code, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and ensure all tests pass.
4. Submit a pull request with a clear description of your changes.

Thank you for considering contributing to our project!

## Outlook

We created a mindmap with possible feature for the future:

<img width="1307" height="959" alt="Image" src="https://github.com/user-attachments/assets/7517a1c2-ec37-496d-89bc-b760b5039d8f" />
