
# Finance Tracker Backend

A secure and scalable REST API for managing personal finances. Built with Java, Spring Boot, PostgreSQL, JWT authentication, and Flyway.

---

## Features

- User registration and login
- JWT authentication & authorization
- Role-based access control (USER / ADMIN)
- Income and expense transaction management
- Custom categories management
- Monthly budgeting system
- Financial summaries and balances
- Soft delete for transactions
- Admin user management
- Database migrations with Flyway
- Production-ready architecture

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Maven
- JWT

---

## Project Structure

```text
src/main/java/com/financetracker
├── config
├── controller.v1
├── service
├── repository
├── entity
├── dto
├── mapper
├── security
└── exception 
```
## Main API Endpoints

### Authentication
- POST `/api/v1/auth/register`
- POST `/api/v1/auth/login`

### Transactions
- GET `/api/v1/transactions`
- GET `/api/v1/transactions/{id}`
- POST `/api/v1/transactions`
- PUT `/api/v1/transactions/{id}`
- DELETE `/api/v1/transactions/{id}`
- GET `/api/v1/transactions/summary`

### Categories
- GET `/api/v1/categories`
- GET `/api/v1/categories/{id}`
- POST `/api/v1/categories`
- PUT `/api/v1/categories/{id}`
- DELETE `/api/v1/categories/{id}`

### Budgets
- GET `/api/v1/budgets`
- GET `/api/v1/budgets/{id}`
- POST `/api/v1/budgets`
- PUT `/api/v1/budgets/{id}`
- DELETE `/api/v1/budgets/{id}`

### Users
- GET `/api/v1/users/me`

### Admin
- GET `/api/v1/admin/users`
- GET `/api/v1/admin/users/{id}`
- PUT `/api/v1/admin/users/{id}/activate`
- PUT `/api/v1/admin/users/{id}/deactivate`

---

## Environment Variables
- DATABASE_URL=jdbc:postgresql://host:5432/dbname
- DATABASE_USERNAME=postgres
- DATABASE_PASSWORD=yourpassword
- JWT_SECRET=your_secret_key
- FRONTEND_URL=`your_frontend_url`

## Database
- PostgreSQL relational database
- Flyway schema migrations
- Tables for users, categories, budgets, and transactions

## Security
- BCrypt password hashing
- JWT token authentication
- Protected endpoints
- Role-based authorization
- Stateless sessions


## Run Locally
- ` ./mvnw spring-boot:run`
