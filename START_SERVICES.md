# Starting All Services - Quick Guide

This guide shows you how to start all microservices and the webapp with a single command.

---

## 🚀 Quick Start (Recommended)

### Option 1: Using Make (Easiest)

```bash
# Start all services (backend + frontend)
make start

# Or use the shorthand
make dev
```

**Other useful commands:**
```bash
make stop          # Stop all services
make restart       # Restart all services
make logs          # View logs
make help          # See all available commands
```

---

### Option 2: Using Shell Script

```bash
# Make the script executable (first time only)
chmod +x start-all.sh

# Start all services
./start-all.sh
```

**To stop:** Press `Ctrl+C`

---

### Option 3: Using Docker Compose

```bash
# Start all services with Docker
make docker-up

# Or directly with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

---

## 📋 What Gets Started

When you run `make start` or `./start-all.sh`, the following services start in order:

### Infrastructure Services (Start First)
1. **Service Discovery** (Eureka) - Port 8761
2. **Config Server** - Port 8888

### Core Services
3. **Admin Server** - Port 9090
4. **API Gateway** - Port 8081 ⭐ (Main entry point)

### Business Services
5. **Auth Service** - Port 8080
6. **Customer Service** - Port 8090
7. **Product Service** - Port 8082
8. **Inventory Service** - Port 8083
9. **Cart Service** - Port 8087
10. **Order Service** - Port 8084
11. **Payment Service** - Port 8085
12. **Notification Service** - Port 8086

### Frontend
13. **Next.js Webapp** - Port 3000

---

## 🌐 Service URLs

After starting, you can access:

| Service | URL |
|---------|-----|
| **Frontend (Webapp)** | http://localhost:3000 |
| **API Gateway** | http://localhost:8081 |
| **Service Discovery** | http://localhost:8761 |
| **Admin Dashboard** | http://localhost:9090 |
| **Config Server** | http://localhost:8888 |

---

## 📝 Logs

Logs are saved in the `./logs/` directory:

```bash
# View all logs
make logs

# Or view specific service log
tail -f logs/product-service.log
tail -f logs/webapp.log
```

---

## 🛑 Stopping Services

### If using Make or Shell Script:
Press `Ctrl+C` in the terminal where services are running

Or from another terminal:
```bash
make stop
```

### If using Docker:
```bash
make docker-down
# or
docker-compose down
```

---

## 🔧 Advanced Usage

### Start Only Backend Services

```bash
# Using Make
make start-backend

# Or using script
chmod +x start-backend.sh
./start-backend.sh
```

### Start Only Webapp

```bash
# Using Make
make start-webapp

# Or manually
cd webapp
bun dev  # or pnpm dev
```

### Install Dependencies

```bash
# Install all dependencies (Maven + Webapp)
make install
```

### Build All Services

```bash
# Build all services
make build
```

### Clean Build Artifacts

```bash
# Clean everything
make clean
```

---

## 🐛 Troubleshooting

### Services won't start

1. **Check Java version:**
   ```bash
   java -version  # Should be 17+
   ```

2. **Check Maven:**
   ```bash
   mvn -version
   ```

3. **Check ports are available:**
   ```bash
   # Check if port 8081 is in use
   lsof -i :8081
   
   # Kill process if needed
   kill -9 <PID>
   ```

### Webapp won't start

1. **Check Node.js:**
   ```bash
   node -version  # Should be 18+
   ```

2. **Install dependencies:**
   ```bash
   cd webapp
   bun install  # or pnpm install
   ```

3. **Check environment variables:**
   ```bash
   cat webapp/.env.local
   # Should have: NEXT_PUBLIC_GATEWAY_URL=http://localhost:8081
   ```

### Services crash on startup

1. **Check logs:**
   ```bash
   tail -f logs/<service-name>.log
   ```

2. **Check database connection:**
   - Ensure PostgreSQL is running (if using local DB)
   - Check connection settings in application.yml

3. **Start services one by one:**
   ```bash
   cd service-discovery
   mvn spring-boot:run
   # Wait for it to start, then start next service
   ```

---

## 📦 Prerequisites

Before starting services, ensure you have:

- ✅ **Java 17+** installed
- ✅ **Maven 3.8+** installed
- ✅ **Node.js 18+** installed
- ✅ **Bun or pnpm** installed (for webapp)
- ✅ **PostgreSQL** running (if not using Docker)
- ✅ **RabbitMQ** running (if not using Docker)

### Quick Check:

```bash
java -version
mvn -version
node -version
bun -version  # or pnpm -version
```

---

## 🐳 Docker Setup

If you prefer Docker (recommended for production):

1. **Build images:**
   ```bash
   make docker-build
   ```

2. **Start services:**
   ```bash
   make docker-up
   ```

3. **View logs:**
   ```bash
   make docker-logs
   ```

4. **Stop services:**
   ```bash
   make docker-down
   ```

---

## 🎯 Recommended Workflow

### For Development:

```bash
# First time setup
make install

# Start all services
make start

# Make changes to code...

# Restart specific service (in another terminal)
cd product-service
mvn spring-boot:run

# Stop all when done
# Press Ctrl+C or run: make stop
```

### For Production:

```bash
# Build production artifacts
make prod-build

# Start with Docker
make docker-up
```

---

## 📚 Additional Resources

- **API Documentation:** See `webapp/API_INTEGRATION.md`
- **Quick Reference:** See `webapp/API_QUICK_REFERENCE.md`
- **Migration Guide:** See `webapp/MIGRATION_GUIDE.md`

---

## 💡 Tips

1. **First startup takes longer** - Services need to register with Eureka
2. **Wait for Service Discovery** - Let Eureka start fully before other services
3. **Check Eureka Dashboard** - Visit http://localhost:8761 to see registered services
4. **Use logs** - Always check logs if something doesn't work
5. **Sequential startup** - The script starts services in the correct order

---

## ✅ Verification

After starting, verify everything is running:

1. **Check Eureka Dashboard:**
   - Visit http://localhost:8761
   - All services should be registered

2. **Check API Gateway:**
   - Visit http://localhost:8081/actuator/health
   - Should return `{"status":"UP"}`

3. **Check Webapp:**
   - Visit http://localhost:3000
   - Should load the homepage

4. **Test API:**
   ```bash
   curl http://localhost:8081/api/v1/product
   ```

---

## 🆘 Getting Help

If you encounter issues:

1. Check the logs in `./logs/`
2. Verify all prerequisites are installed
3. Ensure ports are not already in use
4. Try starting services individually to isolate the problem

---

**Happy Coding! 🚀**
