# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MowItNow Backend is a Spring Boot application that simulates automatic lawn mowers on rectangular surfaces. Following a clean architecture separation, this project focuses exclusively on backend functionality, providing both command-line interface and REST API endpoints.

**Core Business Logic:**
- Mowers navigate on a grid-based lawn using compass directions (N, E, S, W)
- Commands: 'G' (turn left), 'D' (turn right), 'A' (move forward)
- Mowers process commands sequentially and report final positions
- Out-of-bounds movements are ignored, preserving mower position and orientation

**Backend Focus:**
- REST API for remote mower control (`/api/mower/*`)
- Command-line batch processing from file inputs
- Core engine and business logic simulation
- DevSecOps-ready with comprehensive CI/CD pipeline integration

## Architecture

**Package Structure:**
- `com.mowitnow.mower` - Main application entry point
- `com.mowitnow.mower.engine` - Core business logic components

**Key Components:**
- `MowerApplication` - Spring Boot CommandLineRunner that processes file arguments
- `Remote` - Service orchestrating mower operations and file parsing
- `Mower` - Prototype-scoped bean representing individual mowers with UUID identification
- `Lawn` - Cloneable grid boundary manager
- `Compass` - Direction/orientation handler

**Design Patterns:**
- Uses Spring's ObjectProvider for prototype mower instances
- Each mower clones the lawn for isolated state management
- File parsing supports both classpath resources and filesystem paths

## Development Commands

**Build and Package:**

```bash
mvn clean package
```

**Run Tests:**

```bash
mvn test

# Run single test class
mvn test -Dtest=MowerMainApplicationTest

# Run specific test method
mvn test -Dtest=MowerMainApplicationTest#testExecuteBizTest
```

**Run Tests with Coverage:**

```bash
mvn clean test jacoco:report
```

**Coverage Quality Gate (80% minimum):**

```bash
mvn jacoco:check

# Generate detailed coverage report
mvn clean test jacoco:report

# View coverage report at: target/site/jacoco/index.html
```

**Run Application:**

```bash
# With default test file (commandsTest.txt)
java -jar target/mower-0.0.1-SNAPSHOT.jar

# With custom file
java -jar target/mower-0.0.1-SNAPSHOT.jar /path/to/commands.txt
```

**Run in Development Mode:**

```bash
mvn spring-boot:run

# With arguments
mvn spring-boot:run -Dspring-boot.run.arguments="/path/to/commands.txt"
```

**Run as Web Service:**

```bash
# Start web service on port 8080
mvn spring-boot:run

# Or with JAR
java -jar target/mower-0.0.1-SNAPSHOT.jar

# With specific Spring profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Docker Commands

**Build Docker Image:**

```bash
# Build backend image
docker build -t stfgit/mower-java .

# Multi-architecture build (if needed)
docker buildx build --platform linux/amd64,linux/arm64 -t stfgit/mower-java .
```

**Run Docker Container:**

```bash
# Run backend container
docker run -p 8080:8080 stfgit/mower-java

# Run with file mounting for batch processing
docker run -p 8080:8080 -v /path/to/commands:/app/commands stfgit/mower-java /app/commands/myCommands.txt
```

**Docker Security:**
- Runs as non-root user `mower` (UID 1001)
- OpenJDK 21 base image
- Minimal attack surface with single JAR deployment

## REST API Endpoints

The application now includes REST API endpoints for remote mower control:

**Base URL:** `http://localhost:8080/api/mower`

**Endpoints:**

- `GET /api/mower/health` - Health check endpoint
- `POST /api/mower/execute` - Execute single mower command
- `POST /api/mower/batch` - Execute multiple mower commands

**Single Mower Request:**

```json
{
  "lawnDimensions": "5 5",
  "mowerPosition": "1 2 N",
  "commands": "GAGAGAGAA"
}
```

**Batch Mower Request:**

```json
{
  "lawnDimensions": "5 5",
  "mowers": [
    {
      "mowerPosition": "1 2 N",
      "commands": "GAGAGAGAA"
    },
    {
      "mowerPosition": "3 3 E", 
      "commands": "AADAADADDA"
    }
  ]
}
```

**Response Format:**

```json
{
  "mowerId": "uuid-string",
  "x": 1,
  "y": 3,
  "direction": "N",
  "position": "1 3 N"
}
```

## Technology Stack

- **Java 21** - Runtime requirement
- **Spring Boot 3.4.5** - Application framework with Web starter
- **Maven** - Build tool
- **JaCoCo** - Code coverage analysis
- **JUnit 5** - Testing framework
- **AssertJ** - Assertion library
- **SLF4J/Logback** - Logging

## File Format

Input files must follow this structure:
```
5 5          # Lawn dimensions (top-right corner)
1 2 N        # Mower initial position (x y direction)
GAGAGAGAA    # Movement commands
3 3 E        # Second mower position
AADAADADDA   # Second mower commands
```

Expected output:
```
1 3 N
5 1 E
```

## Harness CI/CD Integration

**Primary Pipeline: `mower_demo`** (Recommended)
- **Template-based**: Uses `account.mvn_pipeline` v0.1
- **Repository**: `stfgit/mower-java` on GitHub
- **Branch**: `main`
- **Status**: ✅ Working perfectly - preferred pipeline

**Pipeline Stages:**
1. **Source Code Scan** - OWASP, AquaTrivy, Gitleaks security checks
2. **Maven Build and Push** - Build, test, coverage, Docker image creation
3. **Image Security Scan** - Container vulnerability assessment  
4. **Development Deployment** - Automatic deployment to dev environment
5. **Production Approval** - Manual approval by DevOps team
6. **Production Deployment** - Kubernetes deployment with health checks

**Service Configuration:**
- **Primary Service**: `mower_svc` (Java backend only)
- **Artifacts**: Docker images from DockerHub `stfgit/mower-java`
- **Manifests**: GitHub-based with `values.javaonly.yaml`

**Environments:**
- **development** (PreProduction) - Auto-deploy after successful build
- **prod** (Production) - Manual approval required

**Infrastructure:**
- **Build Cluster**: `account.k8sdocacloud` 
- **Deploy Cluster**: `helm-delegate`
- **Namespace**: `harness-ci`
- **Registry**: DockerHub (`org.Dockerhub` connector)

**Kubernetes Deployment:**

- **Deployment:** 2 replicas with health checks (liveness/readiness probes)
- **Service:** ClusterIP service exposing port 80 → 8080
- **Job:** Batch processing mode for file-based operations
- **Resources:** 128Mi-512Mi memory, 100m-500m CPU limits

## Testing

**Test Resources:**

- `commandsOK.txt` - Valid command set
- `commandsBad.txt` - Invalid format testing
- `commandsOutOfLawn.txt` - Boundary condition testing
- `commandsTest.txt` - Reference test case from requirements

**Key Test:** `testExecuteBizTest()` validates the system against the provided exercise requirements.

**Coverage Requirements:**
- **Minimum Threshold**: 80% line coverage (enforced by JaCoCo)
- **Report Location**: `target/site/jacoco/index.html` (after `mvn jacoco:report`)
- **Quality Gate**: Pipeline fails if coverage drops below 80%

**Test Types:**
- **Unit Tests**: Core engine logic (`Mower`, `Lawn`, `Compass`, `Remote`)
- **Integration Tests**: REST API endpoints (`MowerController`, `WebController`)
- **DTO Tests**: Request/Response validation (`MowerCommandRequest`, `BatchMowerRequest`)
- **Application Tests**: Full Spring Boot context loading

## Web Interface

**Note:** The web interface has been moved to a separate project: `mower-frontend`

The backend application now focuses solely on the REST API and command-line functionality. For the interactive web interface, see the separate `mower-frontend` project repository.

## Spring Boot Configuration

**Application Properties:**
- **Port**: 8080 (default)
- **CORS**: Configured for frontend integration at localhost:3000
- **Logging**: Custom logback configuration with structured output
- **Actuator**: Health endpoints enabled for Kubernetes probes

**Available Profiles:**
- **default**: Production-ready configuration
- **dev**: Development profile with enhanced logging
- **test**: Test environment with in-memory configurations

**Environment Variables:**
- `SERVER_PORT`: Override default port (default: 8080)
- `SPRING_PROFILES_ACTIVE`: Set active Spring profiles
- `LOGGING_LEVEL_ROOT`: Control logging verbosity

## Architecture Notes

**Clean Separation:**
- **Backend (this project)**: REST API, business logic, command-line processing
- **Frontend (mower-frontend)**: Next.js React application for web UI
- **DevSecOps Templates**: Shared Harness templates for both projects

**Integration Points:**
- REST API endpoints for frontend consumption
- CORS configuration for cross-origin requests
- Docker images deployed independently
- Separate CI/CD pipelines for independent releases