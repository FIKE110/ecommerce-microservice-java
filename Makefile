# E-commerce Microservices Makefile
# Provides convenient commands to manage all services

.PHONY: help install build start stop restart logs clean dev docker-up docker-down docker-logs

# Default target
help:
	@echo "E-commerce Microservices - Available Commands:"
	@echo ""
	@echo "  make install       - Install all dependencies (Maven + Webapp)"
	@echo "  make build         - Build all services"
	@echo "  make start         - Start all services (recommended)"
	@echo "  make dev           - Start all services in development mode"
	@echo "  make stop          - Stop all running services"
	@echo "  make restart       - Restart all services"
	@echo "  make logs          - View logs from all services"
	@echo "  make clean         - Clean all build artifacts"
	@echo ""
	@echo "  Docker Commands:"
	@echo "  make docker-up     - Start all services with Docker Compose"
	@echo "  make docker-down   - Stop all Docker containers"
	@echo "  make docker-logs   - View Docker logs"
	@echo "  make docker-build  - Build all Docker images"
	@echo ""
	@echo "  Individual Services:"
	@echo "  make start-backend - Start only backend services"
	@echo "  make start-webapp  - Start only webapp"
	@echo ""

# Install dependencies
install:
	@echo "Installing Maven dependencies..."
	mvn clean install -DskipTests
	@echo "Installing webapp dependencies..."
	cd webapp && (bun install || pnpm install || npm install)
	@echo "✓ All dependencies installed"

# Build all services
build:
	@echo "Building all services..."
	mvn clean package -DskipTests
	@echo "✓ All services built"

# Start all services using the shell script
start:
	@echo "Starting all services..."
	@chmod +x start-all.sh
	@./start-all.sh

# Development mode (same as start)
dev: start

# Stop all services
stop:
	@echo "Stopping all services..."
	@pkill -f "spring-boot:run" || true
	@pkill -f "next-dev" || true
	@pkill -f "pnpm dev" || true
	@pkill -f "bun dev" || true
	@echo "✓ All services stopped"

# Restart all services
restart: stop start

# View logs
logs:
	@if [ -d "logs" ]; then \
		tail -f logs/*.log; \
	else \
		echo "No logs directory found. Start services first."; \
	fi

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	mvn clean
	rm -rf logs/
	cd webapp && rm -rf .next node_modules
	@echo "✓ Cleaned"

# Docker Compose commands
docker-build:
	@echo "Building Docker images..."
	docker-compose build
	@echo "✓ Docker images built"

docker-up:
	@echo "Starting services with Docker Compose..."
	docker-compose up -d
	@echo "✓ All services started"
	@echo ""
	@echo "Service URLs:"
	@echo "  - Frontend:         http://localhost:3000"
	@echo "  - API Gateway:      http://localhost:8000"
	@echo "  - Eureka:           http://localhost:8761"
	@echo "  - Admin Server:     http://localhost:9090"

docker-down:
	@echo "Stopping Docker containers..."
	docker-compose down
	@echo "✓ All containers stopped"

docker-logs:
	docker-compose logs -f

docker-restart: docker-down docker-up

# Start only backend services
start-backend:
	@echo "Starting backend services only..."
	@chmod +x start-backend.sh
	@./start-backend.sh

# Start only webapp
start-webapp:
	@echo "Starting webapp..."
	cd webapp && (bun dev || pnpm dev || npm run dev)

# Quick start (install + start)
quick-start: install start

# Production build
prod-build:
	@echo "Building for production..."
	mvn clean package -DskipTests
	cd webapp && (bun run build || pnpm build || npm run build)
	@echo "✓ Production build complete"
