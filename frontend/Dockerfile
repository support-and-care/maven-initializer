FROM node:22-alpine

# Set working directory
WORKDIR /app

# Install pnpm globally
RUN npm install -g pnpm

# Copy package files
COPY package.json ./

# Install dependencies (without frozen lockfile since we don't have pnpm-lock.yaml)
RUN pnpm install

# Copy source code
COPY . .

# Build the application
RUN pnpm build

# Expose port
EXPOSE 3000

# Start the application
CMD ["pnpm", "start"]
