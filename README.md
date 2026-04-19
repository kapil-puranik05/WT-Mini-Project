# Financial Dashboard

Financial Dashboard is a full-stack web application built to manage and visualize personal finance data in a simple role-based environment. The project combines a Spring Boot backend with a React frontend to demonstrate user authentication, protected APIs, database integration, and interactive dashboard reporting in one complete system.

The application allows users to register with different roles, sign in securely using JWT authentication, view financial summaries, and inspect recent transactions. In addition to regular user access, administrators can create transactions for any user in the system, making the platform useful for both individual records and centralized management scenarios.

## Project Overview

This project focuses on the core ideas commonly used in modern web application development:

- frontend and backend integration
- REST API design
- role-based authorization
- database-driven transaction management
- dashboard-style data presentation

From a functional point of view, the system is designed to:

- record income and expense transactions
- calculate totals such as income, expense, and net balance
- summarize monthly and category-wise activity
- provide different system behavior for `VIEWER`, `ANALYST`, and `ADMIN`
- allow `ADMIN` users to create transactions for any registered user

## Tech Stack

- Backend: Spring Boot 3, Spring Security, Spring Data JPA, Hibernate, PostgreSQL
- Frontend: React, TypeScript, Vite
- Authentication: JWT
- Build tools: Maven for backend, npm for frontend

## Project Structure

```text
financial-dashboard/
|-- financial-dashboard/   # Spring Boot backend
|-- ui/                    # React frontend
|-- README.md
```

## User Roles

- `VIEWER`
  Can view only their own dashboard data.
- `ANALYST`
  Can view the complete system dashboard.
- `ADMIN`
  Can view the complete system dashboard, fetch the full user list, and create transactions for any user.

## Backend Configuration

The backend reads its runtime settings from environment variables referenced in [application.properties](/C:/Users/Kapil/OneDrive/Desktop/Clg%20Stuff/WT/financial-dashboard/financial-dashboard/src/main/resources/application.properties:1).

Required environment variables:

- `JWT_SECRET`
- `JDBC_URL`
- `DB_USER`
- `DB_PASSWORD`

Example:

```env
JWT_SECRET=your-long-jwt-secret-key
JDBC_URL=jdbc:postgresql://localhost:5432/financial_dashboard
DB_USER=postgres
DB_PASSWORD=postgres
```

## How to Run

### Start the backend

From the `financial-dashboard` folder:

```powershell
cd financial-dashboard
mvn spring-boot:run
```

Backend base URL:

```text
http://localhost:8080
```

### Start the frontend

From the `ui` folder:

```powershell
cd ui
npm install
npm run dev
```

The frontend is configured to connect to the backend at `http://localhost:8080`.

## Authentication

- Public APIs are available under `/public/**`
- Protected endpoints require a JWT in the request header

```http
Authorization: Bearer <jwt-token>
```

The JWT currently includes:

- `sub`: user email
- `role`: user role

## Domain Values

### Roles

- `VIEWER`
- `ANALYST`
- `ADMIN`

### Transaction Types

- `INCOME`
- `EXPENSE`

### Categories

- `FOOD`
- `RENT`
- `SALARY`
- `TRAVEL`

## API Endpoints

Base URL: `http://localhost:8080`

### 1. Health Check

**Endpoint**

```http
GET /public/health-check
```

**Authentication**

- Not required

**Response**

```text
Server is running.
```

### 2. Sign Up

**Endpoint**

```http
POST /public/signup
```

**Authentication**

- Not required

**Request Body**

```json
{
  "email": "admin@example.com",
  "password": "password123",
  "role": "ADMIN"
}
```

**Validation**

- `email` must be present and valid
- `password` must be 8 to 15 characters long
- `role` must be one of `VIEWER`, `ANALYST`, `ADMIN`

**Success Response**

```json
{
  "userId": "uuid",
  "createdAt": "2026-04-19T20:15:00"
}
```

### 3. Login

**Endpoint**

```http
POST /public/login
```

**Authentication**

- Not required

**Request Body**

```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```

**Success Response**

```json
{
  "token": "jwt-token"
}
```

### 4. Get All Users

**Endpoint**

```http
GET /users
```

**Authentication**

- Required
- `ADMIN` only

**Success Response**

```json
[
  {
    "userId": "uuid-1",
    "email": "admin@example.com",
    "role": "ADMIN"
  },
  {
    "userId": "uuid-2",
    "email": "viewer@example.com",
    "role": "VIEWER"
  }
]
```

### 5. Create Transaction

**Endpoint**

```http
POST /transactions
```

**Authentication**

- Required
- `ADMIN` only at service level

**Request Body**

```json
{
  "userId": "target-user-uuid",
  "amount": 5000,
  "type": "EXPENSE",
  "category": "FOOD",
  "description": "Team dinner",
  "date": "2026-04-19T19:30:00.000Z"
}
```

**Notes**

- `userId` is required
- `date` can be omitted, in which case the backend uses the current timestamp
- the transaction is created for the selected target user

**Success Response**

Returns the saved transaction entity.

Example:

```json
{
  "transactionId": "uuid",
  "amount": 5000,
  "type": "EXPENSE",
  "category": "FOOD",
  "description": "Team dinner",
  "date": "2026-04-19T19:30:00",
  "user": {
    "userId": "target-user-uuid",
    "email": "viewer@example.com",
    "role": "VIEWER"
  }
}
```

### 6. Get Dashboard

**Endpoint**

```http
GET /transactions/dashboard?month=4&year=2026
```

**Authentication**

- Required
- Accessible to `VIEWER`, `ANALYST`, and `ADMIN`

**Behavior**

- `VIEWER` receives only their own dashboard data
- `ANALYST` and `ADMIN` receive system-wide aggregated dashboard data

**Success Response**

```json
{
  "totalIncome": 100000,
  "totalExpense": 45000,
  "netBalance": 55000,
  "categoryIncome": [
    {
      "category": "SALARY",
      "amount": 100000
    }
  ],
  "categoryExpense": [
    {
      "category": "FOOD",
      "amount": 15000
    },
    {
      "category": "RENT",
      "amount": 30000
    }
  ],
  "monthlyIncome": 100000,
  "monthlyExpense": 45000,
  "recentTransactions": [
    {
      "transactionId": "uuid",
      "amount": 2000,
      "type": "EXPENSE",
      "category": "FOOD",
      "description": "Dinner",
      "date": "2026-04-19T20:00:00"
    }
  ],
  "transactionCount": 12
}
```

## Error Handling

The backend returns structured error responses for handled exceptions:

```json
{
  "message": "Error message",
  "timestamp": "2026-04-19T20:20:00"
}
```

Typical status codes:

- `400 Bad Request`
  Invalid credentials or invalid input
- `403 Forbidden`
  Forbidden operation such as non-admin transaction creation
- `404 Not Found`
  User not found
- `409 Conflict`
  Duplicate signup email
- `500 Internal Server Error`
  Unhandled server-side error

## Frontend Routes

- `/login`
- `/signup`
- `/dashboard`

## Notes

- The frontend reads the authenticated user from the JWT stored in `localStorage`
- The backend uses `spring.jpa.hibernate.ddl-auto=update`
- The frontend base API URL is currently hardcoded to `http://localhost:8080`

## Scope for Enhancement

- add pagination and filtering for transactions
- add a user management view for admins
- improve request validation for transaction payloads
- add refresh tokens and stronger session handling
- add automated backend and frontend tests
