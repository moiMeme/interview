# Spring Boot 3.4.4 + React MVC Example

This is a complete MVC (Model-View-Controller) example using Spring Boot 3.4.4 with Java 21 and React with Vite.

## Architecture Overview

### Backend (Spring Boot)
- **Model**: `ExampleUser` entity with JPA annotations
- **Repository**: `ExampleUserRepository` extending JpaRepository
- **Service**: `ExampleUserService` handling business logic
- **Controller**: `ExampleUserController` exposing REST API at `/api/example/v1`

### Frontend (React + Vite)
- Located in `frontend/` directory
- Built with Vite for fast development
- Automatically integrated into Spring Boot build via Maven

### Database
- H2 in-memory database
- Sample data pre-loaded via `data.sql`
- Console available at: `http://localhost:8080/h2-console`

## API Endpoints

Base URL: `/api/example/v1/users`

- `GET /api/example/v1/users` - Get all users
- `GET /api/example/v1/users/{id}` - Get user by ID
- `POST /api/example/v1/users` - Create new user
- `PUT /api/example/v1/users/{id}` - Update user
- `DELETE /api/example/v1/users/{id}` - Delete user

## Running the Application

### Option 1: Full Build (Backend + Frontend)

```bash
mvn clean install
java -jar target/code-review-challenge-0.0.1-SNAPSHOT.jar
```

Access the application:
- Frontend UI: http://localhost:8080/example-ui
- API: http://localhost:8080/api/example/v1/users
- H2 Console: http://localhost:8080/h2-console

### Option 2: Development Mode

**Backend:**
```bash
mvn spring-boot:run
```

**Frontend (separate terminal):**
```bash
cd frontend
npm install
npm run dev
```

In development mode:
- Backend runs on: http://localhost:8080
- Frontend dev server runs on: http://localhost:3000
- API calls are proxied from frontend to backend

## Project Structure

```
interview/
├── src/main/java/com/interview/
│   └── example/
│       ├── model/
│       │   └── ExampleUser.java          # Entity/Model
│       ├── repository/
│       │   └── ExampleUserRepository.java # Data Access Layer
│       ├── service/
│       │   └── ExampleUserService.java    # Business Logic
│       ├── controller/
│       │   └── ExampleUserController.java # REST API
│       └── config/
│           └── WebConfig.java             # Spring MVC Configuration
├── src/main/resources/
│   ├── application.properties             # H2 Database Config
│   └── data.sql                           # Sample Data
└── frontend/
    ├── src/
    │   ├── App.jsx                        # Main React Component
    │   ├── App.css                        # Styles
    │   ├── main.jsx                       # React Entry Point
    │   └── index.css                      # Global Styles
    ├── index.html                         # HTML Template
    ├── vite.config.js                     # Vite Configuration
    └── package.json                       # NPM Dependencies
```

## Key Configuration Details

### Maven Frontend Plugin (pom.xml)
The frontend build is integrated into Maven lifecycle:
- Installs Node.js and npm automatically
- Runs `npm install` and `npm run build` during Maven build
- Copies built frontend to `target/classes/static/example-ui`

### Spring Boot Static Resources (WebConfig.java)
- Serves React app from `/example-ui` path
- Implements SPA routing (all non-existent paths return `index.html`)
- Allows React Router to handle client-side routing

### Vite Configuration (vite.config.js)
- Base path set to `/example-ui/` to match Spring Boot serving path
- Proxy configured for `/api` requests to backend during development

## Technologies Used

- **Java 21**
- **Spring Boot 3.4.4**
  - spring-boot-starter-web
  - spring-boot-starter-data-jpa
  - spring-boot-starter-validation
- **H2 Database**
- **Lombok**
- **React 18.3**
- **Vite 5.4**
- **Maven 3.x**

## Database Schema

```sql
CREATE TABLE example_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50)
);
```

## Sample Users

The application comes pre-loaded with 5 sample users:
1. John Doe (john.doe@example.com)
2. Jane Smith (jane.smith@example.com)
3. Bob Johnson (bob.johnson@example.com)
4. Alice Williams (alice.williams@example.com)
5. Charlie Brown (charlie.brown@example.com)

## Features Demonstrated

### Backend
- RESTful API design
- MVC pattern implementation
- JPA/Hibernate ORM
- Input validation
- Exception handling
- Service layer pattern
- Repository pattern
- Dependency injection

### Frontend
- React Hooks (useState, useEffect)
- Fetch API for HTTP requests
- CRUD operations UI
- Form handling
- Responsive design
- Error handling

### Integration
- Frontend build automation with Maven
- Single deployable JAR containing both backend and frontend
- CORS configuration
- SPA routing support

## Notes

- The H2 database is in-memory and will reset on each application restart
- Frontend is automatically built and bundled during `mvn clean install`
- For production, consider using a persistent database (PostgreSQL, MySQL, etc.)
- CORS is enabled for all origins in development (`@CrossOrigin(origins = "*")`)
