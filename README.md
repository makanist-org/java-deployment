# Java REST API Project

A simple Spring Boot REST API project with CRUD operations for user management.

## Prerequisites

- Java 1.8 or higher
- Maven 3.6+

## Installation

1. **Install Maven:**
   ```bash
   brew install maven
   ```

2. **Add to PATH (if needed):**
   ```bash
   echo 'export PATH="/opt/homebrew/bin:$PATH"' >> ~/.zshrc
   source ~/.zshrc
   ```

## Project Structure

```
JavaProject/
├── .github/workflows/
│   └── ci.yml               # GitHub Actions CI/CD pipeline
├── src/main/java/com/example/
│   ├── Application.java      # Main Spring Boot application
│   ├── User.java            # User model class
│   ├── UserController.java  # REST controller
│   └── ApiDataController.java # API data controller
├── src/test/java/com/example/
│   └── UserControllerTest.java # JUnit test cases
├── target/                  # Build output (auto-generated)
├── .gitignore              # Git ignore rules
├── pom.xml                 # Maven configuration
└── README.md              # This file
```

## Running the Application

```bash
mvn spring-boot:run
```

Application starts on `http://localhost:8080`

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create new user |
| DELETE | `/api/users/{id}` | Delete user |
| GET | `/api/get_api_data` | Get API data collection |

## Test Cases

### 1. Get All Users (Empty List)
```bash
curl http://localhost:8080/api/users
```
**Expected:** `[]`

### 2. Create User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com"}'
```
**Expected:** `{"id":1,"name":"John Doe","email":"john@example.com"}`

### 3. Create Another User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Smith","email":"jane@example.com"}'
```
**Expected:** `{"id":2,"name":"Jane Smith","email":"jane@example.com"}`

### 4. Get All Users (With Data)
```bash
curl http://localhost:8080/api/users
```
**Expected:** Array with both users

### 5. Get User by ID
```bash
curl http://localhost:8080/api/users/1
```
**Expected:** `{"id":1,"name":"John Doe","email":"john@example.com"}`

### 6. Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```
**Expected:** No content (200 OK)

### 7. Verify Deletion
```bash
curl http://localhost:8080/api/users
```
**Expected:** Array with only Jane Smith

### 8. Get API Data
```bash
curl http://localhost:8080/api/get_api_data
```
**Expected:** JSON array of API data objects

## Building the Project

```bash
# Compile and create JAR file
mvn package
```

## Version Management

```bash
# Check current version
mvn help:evaluate -Dexpression=project.version -q -DforceStdout

# Update version manually
mvn versions:set -DnewVersion=1.0.2

# Auto-increment patch version (1.0.1 → 1.0.2)
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}

# Build with new version
mvn clean package
```

**Version Patterns:**
- `1.0.1` - Bug fixes (patch)
- `1.1.0` - New features (minor)
- `2.0.0` - Breaking changes (major)

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserControllerTest
```

### Test Coverage

The project includes comprehensive JUnit tests:

**UserControllerTest.java:**
- `testCreateUser()` - Tests POST /api/users endpoint
- `testGetAllUsers()` - Tests GET /api/users endpoint
- `testGetUserById()` - Tests GET /api/users/{id} endpoint
- `testDeleteUser()` - Tests DELETE /api/users/{id} endpoint

**Test Features:**
- Uses `@SpringBootTest` for full application context
- MockMvc for HTTP request/response testing
- Ordered test execution with `@TestMethodOrder`
- Detailed HTTP logging with `.andDo(print())`
- JSON path assertions for response validation

## Build Output

After running `mvn package`, the `target/` folder contains:
- `classes/` - Compiled Java classes
- `rest-api-project-1.0.1.jar` - Executable JAR file (version updates automatically)

## GitHub Actions CI/CD

The project includes a 3-stage automated CI/CD pipeline:

**🔧 Setup & Compile** → **🧪 Run Tests** → **📦 Build Package**

- **Stage 1:** Setup JDK, cache dependencies, compile code
- **Stage 2:** Run JUnit tests, publish test results
- **Stage 3:** Build JAR package, upload as artifact

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main`

**Features:**
- Visual flow diagram in GitHub Actions UI
- Artifact sharing between stages
- Test result reporting
- Maven dependency caching for faster builds

**Workflow file:** `.github/workflows/ci.yml`

## Alternative Run Methods

```bash
# Run directly (compiles automatically)
mvn spring-boot:run

# Build JAR first, then run
mvn package
java -jar target/rest-api-project-1.0.1.jar
```