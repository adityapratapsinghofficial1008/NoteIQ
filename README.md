# NoteIQ V1 Backend

NoteIQ is an AI-powered student learning platform backend built using Java 26 and Spring Boot 3.4.3. It provides secure REST APIs for user authentication, PDF study notes upload, document management, AI quiz generation, real-time quiz taking, automated answer evaluation, and quiz attempt history.

---

## Tech Stack
- **Language**: Java 26
- **Framework**: Spring Boot 3.4.3
- **Build Tool**: Maven 3.10.0
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + Stateless JWT Authentication (`io.jsonwebtoken:jjwt`)
- **Password Hashing**: BCrypt
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **PDF Processing**: Apache PDFBox 3.0.4
- **API Documentation**: Springdoc OpenAPI / Swagger UI

---

## Prerequisites & Installation

### 1. Requirements
- Java Development Kit (JDK) 26
- Apache Maven 3.10.0+
- PostgreSQL database server running locally or remotely

### 2. Database Setup
Create a PostgreSQL database named `noteiq`:
```sql
CREATE DATABASE noteiq;
```

### 3. Environment Variables
Copy `.env.example` to your environment or export variables:
```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/noteiq
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
FILE_STORAGE_PATH=./uploads
FRONTEND_URL=http://localhost:5173
```

---

## Build & Execution Commands

### Compile & Build
```bash
mvn clean package
```

### Run Unit Tests
```bash
mvn test
```

### Run Application
```bash
mvn spring-boot:run
```
The server will start on port `8080` at `http://localhost:8080`.

---

## Interactive Swagger API Documentation
Once the application is running, access the Swagger UI in your browser:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

*Note: In Swagger UI, click the **Authorize** button and enter `Bearer <your_jwt_token>` to test protected endpoints.*

---

## REST API Specifications

### 1. Authentication APIs (Public)
| Method | Endpoint | Description | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user | `{ "name": "John", "email": "john@example.com", "password": "password123" }` | `201 Created` (`token`, `user`) |
| `POST` | `/api/auth/login` | Login user & issue JWT | `{ "email": "john@example.com", "password": "password123" }` | `200 OK` (`token`, `user`) |

### 2. User Profile API (Protected)
| Method | Endpoint | Description | Headers | Response |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/users/me` | Get current user details | `Authorization: Bearer <token>` | `200 OK` (`id`, `name`, `email`, `createdAt`) |

### 3. Document APIs (Protected)
| Method | Endpoint | Description | Content-Type | Request Body / Params | Response |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/documents` | Upload PDF document | `multipart/form-data` | `file` (PDF), `title`, `subject`, `chapter` | `201 Created` (`DocumentResponse`) |
| `GET` | `/api/documents` | List user's documents | `application/json` | None | `200 OK` (`List<DocumentResponse>`) |
| `GET` | `/api/documents/{id}` | Get document details | `application/json` | Path variable `id` | `200 OK` (`DocumentResponse`) |
| `DELETE` | `/api/documents/{id}` | Delete document & file | `application/json` | Path variable `id` | `204 No Content` |

### 4. Quiz Generation & Taking APIs (Protected)
| Method | Endpoint | Description | Request Body | Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/quizzes/generate` | Generate Quiz from PDF | `{ "documentId": "...", "numberOfQuestions": 10, "difficulty": "MEDIUM" }` | `201 Created` (`QuizResponse` - *correct answers hidden*) |
| `GET` | `/api/quizzes/{id}` | Retrieve Quiz for taking | None | `200 OK` (`QuizResponse` - *correct answers hidden*) |
| `POST` | `/api/quizzes/{id}/submit` | Submit answers & evaluate | `{ "answers": [ { "questionId": "...", "selectedAnswer": "B" } ] }` | `200 OK` (`QuizResultResponse` with score, percentage & explanations) |

### 5. Quiz History APIs (Protected)
| Method | Endpoint | Description | Request Body | Response |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/quiz-attempts` | List user's quiz attempt history | None | `200 OK` (`List<QuizResultResponse>`) |
| `GET` | `/api/quiz-attempts/{id}` | Detailed review of a specific attempt | None | `200 OK` (`QuizResultResponse`) |

---

## Clean Layered Project Architecture
```
src/main/java/com/noteiq/
├── NoteIqApplication.java
├── config/
│   ├── CorsConfig.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── DocumentController.java
│   ├── QuizAttemptController.java
│   ├── QuizController.java
│   └── UserController.java
├── dto/
│   ├── auth/ (RegisterRequest, LoginRequest, AuthResponse)
│   ├── document/ (DocumentResponse)
│   ├── quiz/ (GenerateQuizRequest, QuizResponse, QuestionResponse, SubmitQuizRequest, QuizResultResponse)
│   └── user/ (UserResponse)
├── entity/
│   ├── Document.java
│   ├── Question.java
│   ├── Quiz.java
│   ├── QuizAnswer.java
│   ├── QuizAttempt.java
│   └── User.java
├── exception/
│   ├── BadRequestException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── repository/
│   ├── DocumentRepository.java
│   ├── QuestionRepository.java
│   ├── QuizAttemptRepository.java
│   ├── QuizRepository.java
│   └── UserRepository.java
├── security/
│   ├── CustomUserDetailsService.java
│   └── JwtAuthenticationFilter.java
└── service/
    ├── AuthService.java
    ├── DocumentService.java
    ├── JwtService.java
    ├── MockQuizGenerationServiceImpl.java
    ├── PdfService.java
    ├── QuizEvaluationService.java
    ├── QuizGenerationService.java
    ├── QuizService.java
    └── UserService.java
```
