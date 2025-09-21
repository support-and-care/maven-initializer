#!/bin/bash

echo "🚀 Starting Maven Initializer Development Servers"
echo "================================================"

# Function to check if a port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        return 0
    else
        return 1
    fi
}

# Check if backend is running
if check_port 8080; then
    echo "✅ Backend is already running on port 8080"
else
    echo "🔨 Starting backend on port 8080..."
    echo "Run this command in a separate terminal:"
    echo "  cd backend && ./mvnw spring-boot:run"
    echo ""
fi

# Check if frontend is running
if check_port 3000; then
    echo "✅ Frontend is already running on port 3000"
else
    echo "🔨 Starting frontend on port 3000..."
    echo "Run this command in a separate terminal:"
    echo "  cd frontend && pnpm dev"
    echo ""
fi

echo "📋 Quick Test Commands:"
echo "  curl http://localhost:8080/api/hello    # Direct backend test"
echo "  curl http://localhost:3000/api/hello    # Frontend proxy test"
echo ""
echo "🌐 Access URLs:"
echo "  Frontend: http://localhost:3000"
echo "  Backend:  http://localhost:8080"
echo "  Health:   http://localhost:8080/api/health"
echo ""
echo "🛠️  Build Commands:"
echo "  ./build.sh  # Build both projects"
echo "  ./test.sh   # Run tests"
