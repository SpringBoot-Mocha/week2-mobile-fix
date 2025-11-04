# MobileFix Service Platform

## Overview
MobileFix is a Spring Boot application that centralizes the lifecycle of mobile device repairs. It exposes secured REST endpoints for customers, technicians, and administrators, and bundles a Thymeleaf dashboard that adapts to the current user's role. Sample data is seeded on startup so the system is ready to explore immediately.

## Tech Stack
- Java 21
- Spring Boot 3 (Web, Data JPA, Security, Validation)
- H2 in-memory database
- Thymeleaf templates
- JUnit 5 and Mockito for unit testing

## Getting Started

### Prerequisites
- JDK 21 or newer available on the `PATH`
- Maven 3.9+ (use the bundled `mvnw` wrapper if Maven is not installed)

### Run the application
```bash
mvn spring-boot:run
```

Once the server starts, open http://localhost:8080/login and sign in with one of the default accounts listed below. The dashboard will render role-specific sections (customer order creation, technician assignment updates, or admin management panels).

### Execute the unit tests
```bash
mvn test
```

## Default Accounts
| Role  | Username | Password  | Notes |
|-------|----------|-----------|-------|
| ADMIN | admin    | admin123  | Full access to user, device, and order management |
| TECH  | tech     | tech123   | Can view assigned orders and update status/notes |
| USER  | user     | user123   | Can register repair requests and manage their own orders |

Passwords are BCrypt-encoded at runtime by the data initializer; the values above are the raw credentials you should enter on the login page.

## Main Routes

### Web UI
- `GET /login` – Authentication form rendered by Thymeleaf
- `GET /dashboard` – Role-aware dashboard (requires authentication)

### REST API
| Method & Path | Description | Access |
|---------------|-------------|--------|
| `GET /api/orders` | Paginated list of repair orders, optional `status` filter | ADMIN, TECH (all orders) / USER (own orders when scoped) |
| `GET /api/orders/my-orders` | Orders for the authenticated customer | USER |
| `GET /api/orders/assigned` | Orders assigned to the authenticated technician | TECH |
| `POST /api/orders` | Create a new repair order from a customer device | USER |
| `PUT /api/orders/{id}/assign/{techId}` | Assign a technician to an order | ADMIN |
| `PUT /api/orders/{id}/status` | Update order status and technician notes | TECH, ADMIN |
| `DELETE /api/orders/{id}` | Cancel an order (customer before work starts, otherwise admin) | USER, ADMIN |
| `GET /api/devices` | List devices or search by `brand`/`model` query params | All authenticated roles |
| `POST /api/devices` | Register a new device model | ADMIN |
| `DELETE /api/devices/{id}` | Remove a device model | ADMIN |
| `GET /api/users` | List users, optional `role` filter | ADMIN |
| `POST /api/users` | Create a new platform user | ADMIN |
| `DELETE /api/users/{id}` | Deactivate a user | ADMIN |

All API endpoints return JSON and leverage Bean Validation for payload verification. Error responses follow a consistent shape defined by the global exception handler.

## Additional Notes
- The application bootstraps an H2 in-memory database; data resets every restart. Enable the H2 console by adding `spring.h2.console.enabled=true` to `application.properties` if you need to inspect tables.
- CORS is not enabled by default. When integrating with a separate frontend, adjust `SecurityConfig` to whitelist the required origins.
- Dashboard actions rely on the REST API via `fetch` calls, so server-side validation messages surface in the UI alerts.