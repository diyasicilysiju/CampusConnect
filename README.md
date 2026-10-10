# 🎓 CampusConnect – College Event Management System

> **Software Testing Course Project (30 Marks)**  
> **Title:** Automated Testing of a College Event Management System Using Selenium WebDriver and TestNG

CampusConnect is a modern, full-stack college event management portal engineered for university students, event organizers, and campus administrators. It has been built with full testability in mind, featuring stable selectors, predictable state transitions, role-based access control, race-condition protection, and a complete TestNG test suite covering 30 real-world automated testing scenarios.

---

## 🚀 Technology Stack

- **Frontend:** HTML5, CSS3, JavaScript (Clean, responsive UI with no heavy frameworks)
- **Backend:** Java 21, Spring Boot 3.2.5
- **Security:** Spring Security, BCrypt password hashing, session management
- **Database:** MySQL 8.0, Spring Data JPA / Hibernate
- **Build Tool:** Apache Maven
- **Test Automation:** Selenium WebDriver 4.20.0, TestNG 7.10.2, WebDriverManager

---

## 📂 Project Structure

```text
campusconnect/
├── pom.xml                               # Maven project dependencies and build plugins
├── schema.sql                            # Standalone SQL schema and table definitions
├── testng.xml                            # TestNG test suite configuration
├── README.md                             # Comprehensive project documentation
├── .gitignore                            # Git ignore patterns
└── src/
    ├── main/
    │   ├── java/com/campusconnect/
    │   │   ├── CampusConnectApplication.java
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java       # Role-based security & endpoint authorization
    │   │   │   └── DataInitializer.java      # Dynamic seed data & test reset runner
    │   │   ├── controller/
    │   │   │   ├── AuthController.java       # Registration, login, session status
    │   │   │   ├── EventController.java      # Event CRUD, search & filtering
    │   │   │   ├── RegistrationController.java# Student RSVP, cancellation & attendee lists
    │   │   │   └── DashboardController.java  # Admin metrics & test reset endpoint
    │   │   ├── dto/                          # Clean data transfer objects with Bean Validation
    │   │   ├── entity/                       # JPA entities: User, Event, Registration
    │   │   ├── exception/                    # Centralized REST exception handlers
    │   │   ├── repository/                   # Spring Data JPA repositories with locking
    │   │   └── service/                      # Business logic, capacity & concurrency checks
    │   └── resources/
    │       ├── application.properties        # Configurable database & server properties
    │       └── static/                       # 8 Frontend logical web pages
    │           ├── css/styles.css            # University portal design system
    │           ├── js/                       # Modular scripts (auth, alerts, filters)
    │           ├── index.html                # Page 1: Home Page & Upcoming Events
    │           ├── login.html                # Page 2: Authentication Page
    │           ├── register.html             # Page 3: Student Registration Page
    │           ├── events.html               # Page 4: Browse, Search & Filter Events
    │           ├── event-details.html        # Page 5: Event Details & RSVP Registration
    │           ├── my-registrations.html     # Page 6: Student Registered Events & Cancellation
    │           ├── admin-dashboard.html      # Page 7: Admin Portal, Statistics & Attendees
    │           └── event-form.html           # Page 8: Unified Create / Edit Event Form
    └── test/
        └── java/com/campusconnect/
            ├── CampusConnectApplicationTests.java
            └── selenium/
                ├── BaseSeleniumTest.java     # Base test with Chrome options & DB reset
                └── CampusConnectSeleniumTests.java # 30 automated test scenarios
```

---

## 🗄️ Database Setup (MySQL)

### 1. Create the Database
Open MySQL command prompt or MySQL Workbench and run:

```sql
CREATE DATABASE IF NOT EXISTS campusconnect_db;
```

### 2. Configure Credentials
The application is pre-configured in `src/main/resources/application.properties` with sensible defaults:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3300}/${DB_NAME:campusconnect_db}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root123}
```

If your MySQL instance runs on the default port `3306` with password `root`, simply override using environment variables or modify `application.properties`:

- In PowerShell:
  ```powershell
  $env:DB_PORT="3306"
  $env:DB_PASSWORD="root"
  ```
- Or pass via Maven command line:
  ```bash
  mvn spring-boot:run -Dspring-boot.run.arguments="--DB_PORT=3306 --DB_PASSWORD=root"
  ```

*(Optional)* You can also run the full schema script provided in `schema.sql`. Spring Boot Hibernate DDL is set to `update` by default, so tables are automatically generated and verified upon startup.

---

## 🔑 Default Credentials & Sample Data

The application automatically seeds realistic college events and accounts on initial launch:

| Role | Name | Email Address | Password | Student ID |
| :--- | :--- | :--- | :--- | :--- |
| **Admin** | System Administrator | `admin@campusconnect.edu` | `Admin@123` | `ADMIN-001` |
| **Student** | John Doe | `john.doe@campusconnect.edu` | `Student@123` | `STU-2024-001` |
| **Student** | Jane Smith | `jane.smith@campusconnect.edu` | `Student@123` | `STU-2024-002` |
| **Student** | Alex Kumar | `alex.kumar@campusconnect.edu` | `Student@123` | `STU-2024-003` |

### Sample Events Seeded:
1. **CodeFest 2026** *(Technical, Capacity: 100)*
2. **AI and Machine Learning Workshop** *(Workshops, Capacity: 50)*
3. **Annual Cultural Fest** *(Cultural, Capacity: 300)*
4. **Inter-College Sports Meet** *(Sports, Capacity: 200)*
5. **Robotics Challenge** *(Technical, Capacity: 40)*
6. **Cybersecurity Awareness Seminar** *(Workshops, Capacity: 80)*
7. **Photography Competition** *(Cultural, Capacity: 2 – Seeded full to test capacity limit)*
8. **Research and Innovation Symposium** *(Technical, Capacity: 60)*
9. **Campus Orientation & Induction 2025** *(Workshops – Seeded in the past to test past-event validation)*

---

## 🛠️ Build and Run Instructions

### 1. Build Application with Maven
```bash
mvn clean package -DskipTests
```

### 2. Run Application
```bash
mvn spring-boot:run
```
Or run the generated JAR:
```bash
java -jar target/campusconnect-1.0.0.jar
```

The application will start on **http://localhost:8080**.

---

## 🌐 Application Pages & Routes

| Page | URL | Description |
| :--- | :--- | :--- |
| **Home Page** | `/index.html` or `/` | Hero section, category navigation, upcoming event cards |
| **Login Page** | `/login.html` | Authentication for students and administrators |
| **Registration Page** | `/register.html` | Student registration with real-time validation |
| **Browse Events** | `/events.html` | Search by title, filter by category and date |
| **Event Details** | `/event-details.html?id=:id` | Event info, capacity counter, one-click RSVP |
| **My Registrations** | `/my-registrations.html` | Student registered events with cancellation option |
| **Admin Dashboard** | `/admin-dashboard.html` | Metrics, event table, attendee viewer, delete modal |
| **Create/Edit Event** | `/event-form.html` | Form for creating and editing events |

---

## 📡 REST API Reference

### Authentication (`/api/auth`)
- `POST /api/auth/register` – Register new student account
- `POST /api/auth/login` – Authenticate student or admin
- `POST /api/auth/logout` – Clear user session
- `GET /api/auth/me` – Current authenticated user status

### Events (`/api/events`)
- `GET /api/events` – Fetch events (query parameters: `search`, `category`, `date`)
- `GET /api/events/{id}` – Fetch single event by ID with available seats
- `POST /api/events` – Create new event (*Admin only*)
- `PUT /api/events/{id}` – Update event details (*Admin only*)
- `DELETE /api/events/{id}` – Delete event and associated RSVPs (*Admin only*)

### Registrations (`/api/registrations`)
- `POST /api/registrations/event/{eventId}` – Register for event (*Student only*)
- `GET /api/registrations/my` – Get logged-in student's registrations (*Student only*)
- `PUT /api/registrations/{id}/cancel` – Cancel an existing registration (*Owner only*)
- `GET /api/registrations/event/{eventId}/students` – View registered students (*Admin only*)

### Dashboard & Utilities
- `GET /api/dashboard/stats` – Metrics for events, students, registrations (*Admin only*)
- `POST /api/data/reset` – Re-initializes clean test data between automation test runs

---

## 🧪 Automated Testing with Selenium WebDriver & TestNG

The project includes an end-to-end automation test suite in `src/test/java/com/campusconnect/selenium/CampusConnectSeleniumTests.java`.

### Running All 30 Test Scenarios

1. Make sure CampusConnect is running on `http://localhost:8080`.
2. In a separate terminal, execute:

```bash
mvn test -Dtest=CampusConnectSeleniumTests
```

To see Google Chrome run in headed browser mode (visible window):
```bash
mvn test -Dtest=CampusConnectSeleniumTests -Dheadless=false
```

### Automated Scenarios Implemented:
1. Student registration with valid data
2. Registration with empty required fields
3. Registration with an invalid email
4. Registration with mismatched passwords
5. Registration with an existing email
6. Login with valid credentials
7. Login with an incorrect password
8. Login with an unknown email
9. Logout
10. Display upcoming events
11. Search events by title
12. Filter events by category
13. Search with no matching results
14. View event details
15. Register for an event successfully
16. Prevent duplicate event registration
17. Prevent registration for a full event
18. Prevent registration for a past event
19. Display My Registrations correctly
20. Cancel an event registration
21. Update available seats after cancellation
22. Create an event as admin
23. Validate an event form with missing fields
24. Reject an invalid event capacity
25. Edit an existing event
26. Delete an event after confirmation
27. View registered students as admin
28. Prevent students from accessing admin-only pages
29. Prevent students from calling admin-only backend endpoints
30. Verify navigation, session behavior, and page refresh behavior

---

## 🏷️ Testability & Stable Element Selectors

Every interactive element uses non-dynamic, deterministic HTML IDs and attributes:

- Inputs: `email`, `password`, `confirmPassword`, `fullName`, `studentId`, `searchInput`, `categoryFilter`, `dateFilter`, `eventTitle`, `eventCategory`, `eventDescription`, `eventDate`, `eventTime`, `eventVenue`, `eventOrganizer`, `eventCapacity`, `eventImageUrl`
- Buttons: `loginButton`, `registerButton`, `eventRegisterButton`, `eventDetailsButton`, `cancelRegistrationButton`, `createEventButton`, `saveEventButton`, `logoutButton`, `confirmCancelButton`, `confirmDeleteButton`
- Message Selectors: `successMessage`, `errorMessage`, `validationMessage`, `emptyEventsMessage`, `registrationStatus`
- Repeated Cards: Class `.event-card` with `data-event-id="ID"`
