# Job Portal

A Spring Boot 3.3.5 / Java 21 job portal with MySQL persistence, stateless JWT authentication, role-based access control, SpringDoc OpenAPI, and a same-origin static HTML/CSS/JavaScript frontend.

## Requirements

Install Java 21, Maven 3.9+ (or use the included Maven wrapper), and MySQL 8+. Create a database user with permission to create and update the `job_portal` schema.

## Configuration

Edit `src/main/resources/application.properties` or provide matching environment-specific overrides for `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, and `jwt.secret`. The default schema configuration uses `spring.jpa.hibernate.ddl-auto=update`, runs an idempotent `schema.sql` bootstrap for the `users` table, and creates the database when the configured MySQL account permits it.

## Run

From the project root, run `./mvnw clean package -DskipTests` and then `java -jar target/job-portal-0.0.1-SNAPSHOT.jar`. Open `http://localhost:8080/`; the root page redirects to the login form. API documentation is available at `http://localhost:8080/swagger-ui/index.html`.

The frontend calls same-origin `/api/**` routes and stores the access token, refresh token, username, and role in browser local storage. The dashboard supports paginated job retrieval, job-seeker applications, employer job posting, and logout. Static pages and assets are permitted by the security filter chain while protected API routes require JWT authentication and method-level roles.

## Main routes

| Area | Routes |
|---|---|
| Authentication | `/api/auth/register`, `/api/auth/login`, `/api/auth/refresh-token`, password reset/change routes |
| Jobs | `/api/jobs`, `/api/jobs/search`, `/api/jobs/post`, update, close, reopen, delete, and employer job routes |
| Applications | `/api/applications/apply`, applicant history, employer applicant review, status updates |
| Profiles | `/api/users/profile`, resume/profile-picture upload, education, experience, and skills |
| Administration | `/api/admin/**` |
| Documentation | `/swagger-ui/index.html`, `/v3/api-docs` |

## Verification

The project was verified with Java 21 using `./mvnw clean test` and `./mvnw clean package -DskipTests`. The test suite uses an isolated in-memory H2 database, so context verification does not require MySQL to be running. The JWT signing key is now persistent and configurable; replace the development value before deployment. The legacy standalone posting page redirects to the authenticated dashboard so it cannot issue unauthenticated or misleading requests.
