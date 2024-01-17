# Fanime

## Navigation
- [Description](#Description)
- [Architecture](#Architecture)
- [Technologies](#Technologies)
- [How to run](#How_to_run)

# Description
## Implemented features

- Authentication and registration with email confirmation
- Password reset possibility
- Anime creation
- Genre creation
- Studio creation

# Architecture
Application is based on microservice architecture:
1. **api-gateway-service**
   - **Description:** Utilizes Spring Cloud Gateway to serve as a single entry point for all incoming requests.
   - **Functionality:** Routes and manages incoming API requests, providing a unified interface to the underlying microservices.
   - **Role:** Acts as the gateway to the entire system, facilitating communication between clients and microservices.

2. **discovery-service**
   - **Description:** Employs Eureka Server for service registry and discovery.
   - **Functionality:**  Enables dynamic service registration and discovery, allowing microservices to locate and communicate with each other.
   - **Role:** Provides a centralized service registry, promoting seamless interaction between microservices.

3. **auth-service**
   - **Description:** Handles user authentication, registration, JWT validation, account verification, and password reset functionalities.
   - **Functionality:**
     - Authentication: Validates user credentials and issues access tokens.
     - Registration: Manages user sign-up and account creation.
     - JWT Validation: Ensures the integrity and validity of JWT tokens.
     - Account Verification: Validates user accounts through a verification process.
     - Reset Password: Allows users to securely reset their passwords.
   - **Integration:** JWT Validation is used by *api-gateway-service* as an AuthenticationFilter for enhanced security.

4. **anime-service**
   - **Description:** Manages anime-related operations, including fetching lists of anime and anime management.
   - **Functionality:**
     - List Fetching: Retrieves and provides information about the available anime.
     - Anime Management: Handles operations related to creating, updating, and deleting anime entries.

5. **mail-service**
   - **Description:** Responsible for sending email messages to users' email addresses.
   - **Functionality:**
     - Email Notifications: Sends account verification and password reset emails to users.

![image](https://github.com/stempz101/fanime-microservices/assets/59826158/ec55885b-1fcf-4384-8b38-50565fec8ef7)

# Technologies
- Java 17
- Spring (Boot, Web, Data, Cloud, Security)
- JUnit 5
- Mockito
- PostgreSQL
- MongoDB
- Apache Kafka
- Docker

# How to run
Using Docker, just run the next command:
```bash
docker compose run -d
```
