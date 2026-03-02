#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

docker_build() {
    echo "Building Docker images..."
    
    echo "Building admin-server..."
    docker build -t ecommerce/admin-server:latest ./admin-server/
    
    echo "Building cart-service..."
    docker build -t ecommerce/cart-service:latest ./cart-service/
    
    echo "Building api-docs..."
    docker build -t ecommerce/api-docs:latest ./api-docs/
    
    echo "✓ All Docker images built"
}

docker_up() {
    echo "Starting all services with Docker Compose..."
    docker-compose up -d
    
    echo ""
    echo "Service URLs:"
    echo "  - Frontend:      http://localhost:3000"
    echo "  - API Gateway:   http://localhost:8081"
    echo "  - Eureka:        http://localhost:8761"
    echo "  - Admin Server:  http://localhost:9090"
    echo "  - Config Server: http://localhost:8888"
}

docker_down() {
    echo "Stopping Docker containers..."
    docker-compose down
    echo "✓ All containers stopped"
}

docker_logs() {
    docker-compose logs -f
}

case "${1:-}" in
    build)
        docker_build
        ;;
    up)
        docker_up
        ;;
    down)
        docker_down
        ;;
    restart)
        docker_down
        docker_up
        ;;
    logs)
        docker_logs
        ;;
    clean)
        echo "Removing Docker containers and images..."
        docker-compose down -v
        docker system prune -f
        echo "✓ Docker cleaned"
        ;;
    *)
        echo "E-commerce Docker Script"
        echo ""
        echo "Usage: $0 {build|up|down|restart|logs|clean}"
        echo ""
        echo "Commands:"
        echo "  build    - Build all Docker images"
        echo "  up       - Start all services with Docker Compose"
        echo "  down     - Stop all Docker containers"
        echo "  restart  - Restart all services"
        echo "  logs     - View Docker logs"
        echo "  clean    - Remove containers and clean up"
        exit 1
        ;;
esac
