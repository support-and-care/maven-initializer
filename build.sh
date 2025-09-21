#!/bin/bash
set -e

echo "🏗️  Building Maven Initializer Full-Stack Application"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if required tools are installed
check_dependencies() {
    echo "🔍 Checking dependencies..."
    
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 21 or higher."
        exit 1
    fi
    
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed. Please install Node.js 18 or higher."
        exit 1
    fi
    
    if ! command -v pnpm &> /dev/null; then
        print_warning "pnpm is not installed. Installing pnpm..."
        npm install -g pnpm
    fi
    
    if ! command -v docker &> /dev/null; then
        print_warning "Docker is not installed. Some features will not be available."
    fi
    
    # Check Java version
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        print_warning "Java version is $JAVA_VERSION. Java 21+ is recommended."
    fi
    
    # Check Node version
    NODE_VERSION=$(node -v | sed 's/v//' | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt 18 ]; then
        print_warning "Node version is $NODE_VERSION. Node 18+ is recommended."
    fi
    
    print_status "All dependencies checked"
}

# Build backend
build_backend() {
    echo "🔨 Building Spring Boot Backend..."
    cd backend
    
    # Make mvnw executable
    chmod +x mvnw
    
    # Clean and compile
    if command -v mvn &> /dev/null; then
        mvn clean compile
        print_status "Backend compiled successfully"
        
        # Package the application
        mvn package -DskipTests
        print_status "Backend packaged successfully"
    else
        print_warning "Maven not found. Please install Maven to build the backend."
    fi
    
    cd ..
}

# Build frontend
build_frontend() {
    echo "🔨 Building Next.js Frontend..."
    cd frontend
    
    # Install dependencies
    pnpm install
    print_status "Frontend dependencies installed"
    
    # Build the application
    pnpm build
    print_status "Frontend built successfully"
    
    cd ..
}

# Build complete project
build_complete() {
    print_status "Build completed successfully!"
}

# Main build process
main() {
    echo "Starting build process..."
    echo "Current directory: $(pwd)"
    echo "Date: $(date)"
    echo ""
    
    check_dependencies
    echo ""
    
    build_backend
    echo ""
    
    build_frontend
    echo ""
    
    build_complete
    echo ""
    
    print_status "🎉 Build completed successfully!"
    echo ""
    echo "📋 Next steps:"
    echo "  • Run './test.sh' to execute tests"
    echo "  • Run './start-dev.sh' to start development servers"
    echo "  • Visit http://localhost:3000 to see the application"
    echo ""
    echo "🚀 Build artifacts:"
    echo "  • Backend JAR: backend/target/backend-1.0.0.jar"
    if [ -d "frontend/.next" ]; then
        echo "  • Frontend build: frontend/.next/"
    fi
    echo ""
    echo "📊 Build summary:"
    echo "  • Backend: $(ls -lh backend/target/*.jar 2>/dev/null | awk '{print $5}' || echo 'N/A')"
    echo "  • Frontend: $(du -sh frontend/.next 2>/dev/null | awk '{print $1}' || echo 'N/A')"
}

# Run main function
main "$@"
