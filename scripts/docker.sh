#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME:-}"
IMAGE_TAG="${IMAGE_TAG:-latest}"

SERVICES=(
    "admin-server"
    "api-docs"
    "api-gateway"
    "auth-service"
    "cart-service"
    "config-server"
    "customer-service"
    "inventory-service"
    "notification-service"
    "order-service"
    "payment-service"
    "product-service"
    "service-discovery"
)

docker_build() {
    echo "Building Docker images..."
    
    for svc in "${SERVICES[@]}"; do
        if [ -d "./$svc" ] && [ -f "./$svc/Dockerfile" ]; then
            echo "Building $svc..."
            docker build -t "ecommerce/$svc:latest" ./$svc/
        fi
    done
    
    echo "✓ All Docker images built"
}

docker_push() {
    if [ -z "$DOCKERHUB_USERNAME" ]; then
        echo "Error: DOCKERHUB_USERNAME environment variable not set"
        echo "Usage: DOCKERHUB_USERNAME=yourusername ./scripts/docker.sh push"
        exit 1
    fi
    
    echo "Pushing images to DockerHub..."
    
    for svc in "${SERVICES[@]}"; do
        if docker image inspect "ecommerce/$svc:$IMAGE_TAG" &> /dev/null; then
            echo "Tagging and pushing $svc..."
            docker tag "ecommerce/$svc:$IMAGE_TAG" "$DOCKERHUB_USERNAME/ecommerce-$svc:$IMAGE_TAG"
            docker push "$DOCKERHUB_USERNAME/ecommerce-$svc:$IMAGE_TAG"
        fi
    done
    
    echo "✓ All images pushed to DockerHub"
    echo ""
    echo "Image URLs:"
    for svc in "${SERVICES[@]}"; do
        echo "  - $DOCKERHUB_USERNAME/ecommerce-$svc:$IMAGE_TAG"
    done
}

docker_build_push() {
    docker_build
    docker_push
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
    push)
        docker_push
        ;;
    build-push)
        docker_build_push
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
        echo "Usage: $0 {build|push|build-push|up|down|restart|logs|clean}"
        echo ""
        echo "Commands:"
        echo "  build       - Build all Docker images"
        echo "  push        - Push images to DockerHub"
        echo "  build-push  - Build and push to DockerHub"
        echo "  up          - Start all services with Docker Compose"
        echo "  down        - Stop all Docker containers"
        echo "  restart     - Restart all services"
        echo "  logs        - View Docker logs"
        echo "  clean       - Remove containers and clean up"
        echo ""
        echo "Environment Variables:"
        echo "  DOCKERHUB_USERNAME - Your DockerHub username (required for push)"
        echo "  IMAGE_TAG          - Image tag (default: latest)"
        exit 1
        ;;
esac
