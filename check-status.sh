#!/bin/bash

# Service Status Checker
# Checks if all services are running and healthy

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

check_service() {
    local name=$1
    local url=$2
    
    if curl -s -f "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} $name - Running"
        return 0
    else
        echo -e "${RED}✗${NC} $name - Not responding"
        return 1
    fi
}

print_header "Checking Service Status"

# Check backend services
check_service "Service Discovery (Eureka)" "http://localhost:8761/actuator/health"
check_service "Config Server" "http://localhost:8888/actuator/health"
check_service "Admin Server" "http://localhost:9090/actuator/health"
check_service "API Gateway" "http://localhost:8081/actuator/health"
check_service "Auth Service" "http://localhost:8080/actuator/health"
check_service "Product Service" "http://localhost:8082/actuator/health"
check_service "Cart Service" "http://localhost:8087/actuator/health"
check_service "Order Service" "http://localhost:8084/actuator/health"
check_service "Customer Service" "http://localhost:8090/actuator/health"
check_service "Payment Service" "http://localhost:8085/actuator/health"
check_service "Inventory Service" "http://localhost:8083/actuator/health"
check_service "Notification Service" "http://localhost:8086/actuator/health"

echo ""
print_header "Checking Frontend"

if curl -s -f "http://localhost:3000" > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Webapp - Running"
else
    echo -e "${RED}✗${NC} Webapp - Not responding"
fi

echo ""
print_header "Quick Links"
echo "  Webapp:            http://localhost:3000"
echo "  API Gateway:       http://localhost:8081"
echo "  Eureka Dashboard:  http://localhost:8761"
echo "  Admin Dashboard:   http://localhost:9090"
echo ""
