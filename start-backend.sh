#!/bin/bash

# Backend Services Only Startup Script
# Starts all backend microservices without the webapp

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Services in startup order
SERVICES=(
    "service-discovery"
    "config-server"
    "admin-server"
    "api-gateway"
    "auth-service"
    "customer-service"
    "product-service"
    "inventory-service"
    "cart-service"
    "order-service"
    "payment-service"
    "notification-service"
)

declare -a PIDS

cleanup() {
    print_warning "Shutting down all backend services..."
    for pid in "${PIDS[@]}"; do
        kill "$pid" 2>/dev/null || true
    done
    exit 0
}

trap cleanup SIGINT SIGTERM

mkdir -p logs

print_info "Starting backend services..."

for service in "${SERVICES[@]}"; do
    if [ -d "$service" ]; then
        print_info "Starting $service..."
        cd "$service"
        mvn spring-boot:run > "../logs/${service}.log" 2>&1 &
        PIDS+=($!)
        cd ..
        print_success "$service started"
        sleep 5
    fi
done

print_success "All backend services started!"
print_info "API Gateway available at: http://localhost:8081"
print_warning "Press Ctrl+C to stop all services"

wait
