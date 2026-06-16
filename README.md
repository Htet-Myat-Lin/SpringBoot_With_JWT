# JWT Spring Boot Authentication API
A Spring Boot REST API application featuring JWT (Json Web Token) authentication and authorization with role-based access control.


## Overview
This project implements an authentication system using Spring Security and JWT tokens. It provides user registration, login, and role-based access control with MySQL database.


## Installation & Setup
1. Clone the repository:
```
git clone https://github.com/your-username/jwt-spring.git
```

2. Run the application
```
mvn spring-boot:run
```
The application will start on http://localhost:8090


## Tech Stack
* **Java Version:** 25
* **Framework:** Spring Boot 4.1.0
* **Database:** MySQL
* **Security:** Spring Security 6
* **ORM:** Spring Data JPA & Hibernate
* **Build Tool:** Maven
* **JWT Library:** JJWT 0.12.6


## API Endpoints
### User Registration
```
POST /api/auth/register
{
    "username": "John",
    "email": "john@gmail.com",
    "password": "password123"
}
```

### User Login
```
POST /api/auth/login
{
    "email": "john@gmail.com",
    "password": "password123"
}
```

