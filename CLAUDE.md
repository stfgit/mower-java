# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MowItNow is a Spring Boot application that simulates automatic lawn mowers on rectangular surfaces. The application implements a command-line interface for processing mower instructions from files and can run as a standalone JAR.

**Core Business Logic:**
- Mowers navigate on a grid-based lawn using compass directions (N, E, S, W)
- Commands: 'G' (turn left), 'D' (turn right), 'A' (move forward)
- Mowers process commands sequentially and report final positions
- Out-of-bounds movements are ignored, preserving mower position and orientation

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
```

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

**Pipeline Structure:**

- Security scanning (OWASP, AquaTrivy, Gitleaks)
- Maven build and test with coverage verification
- Docker image build and push to DockerHub (stfgit/mower-java)
- Container security scanning
- Deployment to dev environment (automatic)
- Manual approval gate for production
- Production deployment

**Environments:**

- **dev** - Development environment with mower_infra
- **prod** - Production environment with approval workflow

**Infrastructure:**

- Kubernetes clusters: k8sdocacloud (builds), helm-delegate (deployments)
- Docker registry: DockerHub (stfgit/mower-java)
- Namespace: harness-ci

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

## Web Interface

The application includes a modern web interface for interactive mower control:

**Access:** `http://localhost:8080/`

**Features:**

- **Interactive Lawn Grid:** Visual representation with clickable cells
- **Mower Placement:** Click on grid or use position controls
- **Manual Control:** Individual command buttons (Left/Right/Forward)
- **Sequence Mode:** Execute command strings like "GAGAGAGAA"
- **Batch Mode:** Multiple mowers with different starting positions
- **Real-time Visualization:** Live mower position and path tracking
- **Responsive Design:** Works on desktop and mobile devices

**Interface Components:**

- Lawn dimension configuration (1x1 to 20x20)
- Visual mower with directional arrows (↑ ↓ ← →)
- Path tracing showing mower's journey
- Results panel with timestamped operations
- Error handling and status feedback

## Frontend Development

The project includes a Next.js frontend in `mower-frontend/` with TypeScript and TailwindCSS:

**Frontend Commands:**

```bash
cd mower-frontend

# Development (uses Turbopack for faster builds)
npm run dev

# Build and production
npm run build
npm run start

# Linting
npm run lint
```

**Frontend Technologies:**

- Next.js 15.4.5 with App Router
- React 19.1.0
- TypeScript 5
- TailwindCSS 4
- Turbopack for development builds

**Docker Build:**

```bash
# Backend image
docker build -t stfgit/mower-java .

# Frontend image (from mower-frontend directory)
cd mower-frontend
docker build -t stfgit/mower-java-frontend .
```