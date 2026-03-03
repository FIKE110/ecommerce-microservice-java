#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "=========================================="
echo "  E-commerce Build Script"
echo "=========================================="

build_backend() {
    echo ""
    echo "[1/2] Building backend microservices..."
    cd "$PROJECT_DIR"
    mvn clean install -DskipTests -pl utils
    mvn package -DskipTests
    echo "✓ Backend built successfully"
}

build_frontend() {
    echo ""
    echo "[2/2] Building frontend webapp..."
    cd "$PROJECT_DIR/webapp"
    
    if command -v bun &> /dev/null; then
        echo "Using bun to build webapp..."
        bun install
        bun run build
    elif command -v pnpm &> /dev/null; then
        echo "Using pnpm to build webapp..."
        pnpm install
        pnpm build
    else
        echo "Using npm to build webapp..."
        npm install
        npm run build
    fi
    
    echo "✓ Frontend built successfully"
}

case "${1:-all}" in
    backend)
        build_backend
        ;;
    frontend)
        build_frontend
        ;;
    all)
        build_backend
        build_frontend
        echo ""
        echo "=========================================="
        echo "  Build Complete!"
        echo "=========================================="
        ;;
    clean)
        echo "Cleaning build artifacts..."
        cd "$PROJECT_DIR"
        mvn clean
        rm -rf webapp/.next webapp/node_modules
        echo "✓ Cleaned"
        ;;
    *)
        echo "Usage: $0 {backend|frontend|all|clean}"
        exit 1
        ;;
esac
