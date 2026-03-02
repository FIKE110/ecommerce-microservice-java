#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

ENV="${1:-local}"
DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME:-}"
REGISTRY="${REGISTRY:-docker.io}"
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
    
    for svc in "${SERVICES[@]}"; do
        if [ -d "./$svc" ] && [ -f "./$svc/Dockerfile" ]; then
            echo "Building $svc..."
            docker build -t "ecommerce/$svc:$IMAGE_TAG" ./$svc/
        fi
    done
    
    echo "✓ Docker images built"
}

dockerhub_push() {
    if [ -z "$DOCKERHUB_USERNAME" ]; then
        echo "Error: DOCKERHUB_USERNAME environment variable not set"
        echo ""
        echo "Usage: DOCKERHUB_USERNAME=yourusername ./scripts/deploy.sh dockerhub"
        exit 1
    fi
    
    echo ""
    echo "[3/3] Pushing images to DockerHub..."
    
    for svc in "${SERVICES[@]}"; do
        if docker image inspect "ecommerce/$svc:$IMAGE_TAG" &> /dev/null; then
            echo "Pushing $svc..."
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

deploy_local() {
    echo "Deploying to local environment..."
    
    ./scripts/build.sh all
    
    for svc in "${SERVICES[@]}"; do
        if [ -d "./$svc" ] && [ -f "./$svc/Dockerfile" ]; then
            echo "Building $svc..."
            docker build -t "ecommerce/$svc:$IMAGE_TAG" ./$svc/
        fi
    done
    
    docker-compose up -d
    
    echo ""
    echo "✓ Deployed to local environment"
    echo "  - Frontend:      http://localhost:3000"
    echo "  - API Gateway:   http://localhost:8081"
}

deploy_dockerhub() {
    echo "Deploying to DockerHub..."
    
    docker_build_all
    dockerhub_push
    
    echo ""
    echo "✓ DockerHub deployment complete"
    echo "  Username: $DOCKERHUB_USERNAME"
    echo "  Tag: $IMAGE_TAG"
}

deploy_prod() {
    echo "Deploying to production..."
    
    docker_build_all
    
    echo ""
    echo "[3/3] Pushing images to registry..."
    
    for svc in "${SERVICES[@]}"; do
        echo "Pushing $svc..."
        docker tag "ecommerce/$svc:$IMAGE_TAG" "$REGISTRY/ecommerce-$svc:$IMAGE_TAG"
        docker push "$REGISTRY/ecommerce-$svc:$IMAGE_TAG"
    done
    
    echo "✓ Production deployment complete"
    echo "  Registry: $REGISTRY"
    echo "  Tag: $IMAGE_TAG"
}

case "$ENV" in
    local)
        deploy_local
        ;;
    dockerhub)
        deploy_dockerhub
        ;;
    prod|production)
        deploy_prod
        ;;
    build)
        build_all
        ;;
    *)
        echo "E-commerce Deployment Script"
        echo ""
        echo "Usage: $0 {local|dockerhub|prod|build} [options]"
        echo ""
        echo "Environments:"
        echo "  local      - Build and deploy locally"
        echo "  dockerhub  - Build and push to DockerHub"
        echo "  prod       - Build and push to custom registry"
        echo "  build      - Build application only"
        echo ""
        echo "Environment Variables:"
        echo "  DOCKERHUB_USERNAME - Your DockerHub username (required for dockerhub)"
        echo "  REGISTRY           - Custom registry URL (default: docker.io)"
        echo "  IMAGE_TAG          - Image tag (default: latest)"
        exit 1
        ;;
esac
