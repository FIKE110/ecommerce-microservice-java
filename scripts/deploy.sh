#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

ENV="${1:-local}"
REGISTRY="${REGISTRY:-localhost:5000}"
IMAGE_TAG="${IMAGE_TAG:-latest}"

echo "=========================================="
echo "  E-commerce Deployment Script"
echo "  Environment: $ENV"
echo "=========================================="

build_all() {
    echo ""
    echo "[1/3] Building application..."
    ./scripts/build.sh all
    echo "✓ Build complete"
}

docker_build_all() {
    echo ""
    echo "[2/3] Building Docker images..."
    
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
    
    for svc in "${SERVICES[@]}"; do
        if [ -d "./$svc" ] && [ -f "./$svc/Dockerfile" ]; then
            echo "Building $svc..."
            docker build -t "$REGISTRY/ecommerce-$svc:$IMAGE_TAG" ./$svc/
        fi
    done
    
    echo "✓ Docker images built"
}

docker_push() {
    echo ""
    echo "[3/3] Pushing images to registry..."
    
    for svc in "${SERVICES[@]}"; do
        echo "Pushing $svc..."
        docker push "$REGISTRY/ecommerce-$svc:$IMAGE_TAG"
    done
    
    echo "✓ Images pushed to registry"
}

deploy_local() {
    echo "Deploying to local environment..."
    
    ./scripts/build.sh all
    
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
    
    for svc in "${SERVICES[@]}"; do
        if [ -d "./$svc" ] && [ -f "./$svc/Dockerfile" ]; then
            echo "Building $svc..."
            docker build -t "ecommerce/$svc:latest" ./$svc/
        fi
    done
    
    docker-compose up -d
    
    echo ""
    echo "✓ Deployed to local environment"
    echo "  - Frontend:      http://localhost:3000"
    echo "  - API Gateway:   http://localhost:8081"
}

deploy_prod() {
    echo "Deploying to production..."
    
    if [ -z "$REGISTRY" ]; then
        echo "Error: REGISTRY environment variable not set"
        exit 1
    fi
    
    docker_build_all
    docker_push
    
    echo ""
    echo "✓ Production deployment complete"
    echo "  Registry: $REGISTRY"
    echo "  Tag: $IMAGE_TAG"
}

case "$ENV" in
    local)
        deploy_local
        ;;
    prod|production)
        deploy_prod
        ;;
    build)
        build_all
        ;;
    docker-push)
        docker_build_all
        docker_push
        ;;
    *)
        echo "E-commerce Deployment Script"
        echo ""
        echo "Usage: $0 {local|prod|build|docker-push} [options]"
        echo ""
        echo "Environments:"
        echo "  local     - Build and deploy locally (default)"
        echo "  prod      - Build, push to registry for production"
        echo ""
        echo "Commands:"
        echo "  build       - Build application only"
        echo "  docker-push - Build and push Docker images"
        echo ""
        echo "Options:"
        echo "  REGISTRY=<url>    Docker registry URL"
        echo "  IMAGE_TAG=<tag>   Image tag (default: latest)"
        exit 1
        ;;
esac
