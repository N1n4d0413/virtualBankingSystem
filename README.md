# Virtual Banking System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-Build%20Tool-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)

---

## Short Description

Virtual Banking System is a learning-oriented Spring Boot project that simulates core online banking workflows for both customers and administrators. It demonstrates CRUD-based backend development, relational data modeling, REST API design, and form-driven interaction through static HTML pages. The project is intentionally scoped as a practical academic portfolio piece rather than a production banking platform.

---

## Problem Statement

Many students learn web development in isolated pieces without building a complete, end-to-end business application. This project addresses that gap by modeling a simplified banking system where users can create accounts, manage balances, perform transactions, and receive system notifications while administrators can manage customer accounts.

## Solution

The application provides a Spring Boot backend with JPA repositories and MySQL persistence to support account and transaction workflows. It separates customer and admin actions, stores transaction history, and exposes REST endpoints that can be consumed by the bundled static frontend pages.

---

## Features

- Customer authentication with sign-up and login
- Admin authentication with sign-up and login
- Account dashboard and profile details
- Deposit, withdrawal, and fund transfer
- Passbook / transaction history
- Notifications for account activity and admin actions
- Password recovery using security questions
- Password update from the profile flow
- Admin customer management: add users, search and filter users, freeze and unfreeze accounts, and delete users with balance checks
- Account deletion request flow for customers
- Startup routine that resets transaction counters for the demo month simulation

## ??? System Architecture

<p align="center">
  <img src="assets/arch.jpg" alt="System Architecture" width="900">
</p>

---

## Tech Stack

- Backend: Spring Boot 3.5.9
- Language: Java 17
- Persistence: Spring Data JPA / Hibernate
- Database: MySQL
- Build tool: Maven
- Utility library: Lombok
- Frontend: Static HTML, CSS, and JavaScript pages served from `src/main/resources/static`

## Project Architecture

The codebase follows a simple layered REST architecture:

- `controller` contains HTTP endpoints for customer, transaction, notification, and admin flows
- `dto` defines request and response payloads
- `models` contains JPA entities mapped to the database
- `repos` provides Spring Data JPA repository access
- `config` contains startup behavior, including the transaction counter reset
- `static` contains the browser-facing HTML pages

---

## Folder Structure

```text
.
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- assets
|   `-- arch.jpg
`-- src
    |-- main
    |   |-- java
    |   |   `-- com/vbs/demo
    |   |       |-- config
    |   |       |-- controller
    |   |       |-- dto
    |   |       |-- models
    |   |       `-- repos
    |   `-- resources
    |       |-- application.properties
    |       `-- static
    |           |-- admin.html
    |           |-- adminhistory.html
    |           |-- dashboardf-.html
    |           |-- dashboardf.html
    |           |-- forgot-passwordf.html
    |           |-- homef.html
    |           |-- loginf.html
    |           |-- passbookf.html
    |           |-- signupf.html
    |           |-- theme.js
    |           `-- view-profilef.html
    `-- test
        `-- java
            `-- com/vbs/demo
                `-- VbsApplicationTests.java
```

---

## Installation

1. Install JDK 17.
2. Install and start MySQL.
3. Create a database named `vbsdb` or update the database name in `application.properties`.
4. Open the project in your IDE or terminal.
5. Ensure the Maven wrapper is available in the repository root.
6. Update the database credentials in `src/main/resources/application.properties`.

## Configuration

Edit `src/main/resources/application.properties` before running the app:

- `spring.datasource.url` should point to your MySQL database
- `spring.datasource.username` should match your MySQL user
- `spring.datasource.password` should match your MySQL password
- `spring.jpa.hibernate.ddl-auto=update` keeps the schema in sync during development
- `server.port=8082` sets the application port

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vbsdb
spring.datasource.username=root
spring.datasource.password=your-password
server.port=8082
```

## Running the Project

Run the application with the Maven wrapper:

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

After startup, open the application on port `8082`. The static pages in `src/main/resources/static` are served by Spring Boot.

---

## API Overview

The backend is organized into a small set of REST modules:

- Authentication and account onboarding for customers and admins
- Customer dashboard and profile lookup
- Transactions for deposit, withdrawal, transfer, and passbook history
- Notification retrieval for customers and admins
- Admin operations for adding, searching, filtering, freezing, unfreezing, and deleting users
- Password recovery flows using security questions

---

## Screenshots

- Login  
  ![Login](assets/login.png)

- Dashboard  
  ![Dashboard](assets/dashboard.png)

- Transactions  
  ![Transactions](assets/transactions.png)

- Admin Panel  
  ![Admin Panel](assets/admin-panel.png)

## Demo

Demo video placeholder: coming soon.

---

## Future Improvements

- Add password hashing instead of storing plain-text passwords
- Introduce role-based access control and server-side session handling
- Add validation, centralized error handling, and cleaner API responses
- Move the monthly transaction reset to a scheduled job instead of application restart
- Expand automated tests for controller and repository behavior

## Learning Outcomes

This project demonstrates practical experience with Spring Boot, REST API design, JPA entity mapping, repository-based data access, and basic transaction logic. It also shows how to connect backend endpoints with static frontend pages and how to structure a small application into clear layers.

---

## License

MIT License placeholder.

## Author

Author: Ninad Kathe


