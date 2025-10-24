# Cinema Seat Reservation System

[![version](https://img.shields.io/badge/version-1.3.7-yellow.svg)](https://semver.org)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![Build](https://github.com/rkociniewski/csrs/actions/workflows/main.yml/badge.svg)](https://github.com/rkociniewski/fibonacci/actions/workflows/main.yml)
[![CodeQL](https://github.com/rkociniewski/gac/actions/workflows/codeql.yml/badge.svg)](https://github.com/rkociniewski/crs/actions/workflows/codeql.yml)
[![Dependabot Status](https://img.shields.io/badge/Dependabot-enabled-success?logo=dependabot)](https://github.com/rkociniewski/crs/network/updates)
[![codecov](https://codecov.io/gh/rkociniewski/crs/branch/main/graph/badge.svg)](https://codecov.io/gh/rkociniewski/fibonacci)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-blueviolet?logo=kotlin)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-9.10-blue?logo=gradle)](https://gradle.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

**Cinema Seat Reservation System (CSRS)** is a modern REST API for cinema ticket booking built with Kotlin Coroutines
and Micronaut Framework. The system demonstrates asynchronous programming, concurrent request handling, and real-time
seat availability management with automatic reservation expiration.

## ✨ Features

* 🎟️ **Multi-ticket reservation** - Reserve multiple seats with different ticket types (child, senior, standard)
* ⏱️ **Time-based holds** - Seats are temporarily reserved with configurable timeout (default: 15 minutes)
* 🔄 **Automatic expiration** - Scheduled task releases expired reservations every minute
* 🚀 **Async operations** - All endpoints use Kotlin Coroutines for non-blocking I/O
* 🎭 **Real-time availability** - Concurrent seat checking prevents double-booking
* 💳 **Payment simulation** - Confirm or cancel reservations within timeout window
* 📊 **REST-ful API** - Clean HTTP endpoints with proper status codes and error handling
* 🗄️ **PostgresSQL backend** - Reliable persistence with Flyway migrations

## 🎫 Business Flow

1. **Browse movies** - View available films and screening times
2. **Select screening** - Choose specific date, time, and hall
3. **Check availability** - See all seats with real-time availability status
4. **Reserve seats** - Select multiple seats with ticket types:
    - `CHILD_DISCOUNT` - Discounted for children
    - `SENIOR_DISCOUNT` - Discounted for seniors
    - `STANDARD` - Regular price
5. **Time-limited hold** - Seats reserved for X minutes (configurable, default: 15)
6. **Complete payment** - Confirm within timeout to secure tickets
7. **Auto-release** - Unpaid reservations automatically cancelled after timeout

## 🏗️ Architecture

### Technology Stack

* **Kotlin 2.2.20** - Modern, concise JVM language
* **Micronaut 4.x** - Lightweight framework with fast startup
* **Kotlin Coroutines** - Asynchronous, non-blocking operations
* **Micronaut Data JDBC** - Type-safe database access
* **PostgresSQL** - Production-grade relational database
* **Flyway** - Version-controlled database migrations
* **HikariCP** - High-performance connection pooling

### Project Structure

```
📦 rk.powermilk.cinema
 ┣ 📁 configuration      # Application config (reservation timeout)
 ┣ 📁 controller         # REST API endpoints
 ┃ ┣ MovieController
 ┃ ┣ ScreeningController
 ┃ ┗ ReservationController
 ┣ 📁 dto                # Data Transfer Objects
 ┣ 📁 enums              # Domain enums (ReservationState, TicketType)
 ┣ 📁 exception          # Global exception handlers
 ┣ 📁 model              # JPA entities
 ┃ ┣ Customer
 ┃ ┣ Movie
 ┃ ┣ Hall
 ┃ ┣ Seat
 ┃ ┣ Screening
 ┃ ┣ Reservation
 ┃ ┗ ReservedSeat
 ┣ 📁 repository         # Database access layer
 ┣ 📁 service            # Business logic
 ┃ ┣ MovieService
 ┃ ┣ ScreeningService
 ┃ ┗ ReservationService
 ┣ 📁 task               # Scheduled jobs
 ┃ ┗ ReservationExpirationTask
 ┗ 📜 Application.kt
```

## 🚀 Getting Started

### Requirements

* JDK 21+
* Docker (for PostgresSQL)
* Gradle 9.10

### Installation

1. Clone the repository:

   ```bash
   git clone git@github.com:rkociniewski/csrs.git
   cd csrs
   ```

2. Start PostgreSQL with Docker:

   ```bash
   docker run -d \
     --name csrs-postgres \
     -e POSTGRES_DB=cinema \
     -e POSTGRES_USER=cinema \
     -e POSTGRES_PASSWORD=cinema \
     -p 5432:5432 \
     postgres:16
   ```

3. Configure database connection in `application.yaml`:

   ```yaml
   datasources:
     default:
       url: jdbc:postgresql://localhost:5432/cinema
       username: cinema
       password: cinema
       driverClassName: org.postgresql.Driver
   ```

4. Run the application:

   ```bash
   ./gradlew run
   ```

   The API will be available at `http://localhost:8080`

## 📡 API Endpoints

### Movies

```http
GET    /api/movies           # List all movies
GET    /api/movies/{id}      # Get movie details
```

### Screenings

```http
GET    /api/screenings                # List all screenings
GET    /api/screenings/{id}           # Get screening details
GET    /api/screenings/{id}/seats     # Check seat availability
GET    /api/screenings/movie/{movieId} # Screenings for specific movie
```

### Reservations

```http
POST   /api/reservations              # Create new reservation
GET    /api/reservations/{id}         # Get reservation details
POST   /api/reservations/{id}/payment # Confirm payment
DELETE /api/reservations/{id}         # Cancel reservation
GET    /api/reservations/customer/{customerId} # Customer's reservations
```

### Example: Create Reservation

```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "screeningId": 1,
    "seats": {
      "5": "CHILD_DISCOUNT",
      "6": "STANDARD",
      "7": "SENIOR_DISCOUNT"
    }
  }'
```

**Response:**

```json
{
    "id": 1,
    "screeningId": 1,
    "movieTitle": "The Matrix",
    "hallName": "Sala 1",
    "startTime": "2025-10-17T19:00:00",
    "customerName": "Alice Liddell",
    "customerEmail": "alice@example.com",
    "seats": [
        {
            "seatId": 5,
            "row": "A",
            "number": 5,
            "ticketType": "CHILD_DISCOUNT"
        },
        {
            "seatId": 6,
            "row": "A",
            "number": 6,
            "ticketType": "STANDARD"
        },
        {
            "seatId": 7,
            "row": "A",
            "number": 7,
            "ticketType": "SENIOR_DISCOUNT"
        }
    ],
    "state": "RESERVED",
    "createdAt": "2025-10-16T14:30:00",
    "expiresAt": "2025-10-16T14:45:00"
}
```

### Example: Confirm Payment

```bash
curl -X POST http://localhost:8080/api/reservations/1/payment \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "CARD",
    "transactionId": "TXN123456"
  }'
```

## ⚙️ Configuration

Edit `src/main/resources/application.yaml`:

```yaml
csrs:
    reservation:
        timeout-minutes: 15  # Seat hold duration (default: 15 minutes)

micronaut:
    application:
        name: csrs
    task:
        enabled: true  # Enable scheduled tasks

datasources:
    default:
        url: jdbc:postgresql://localhost:5432/cinema
        username: cinema
        password: cinema

flyway:
    datasources:
        default:
            enabled: true
```

## 🗄️ Database Schema

### Key Tables

* **movie** - Film catalog (title, duration)
* **hall** - Cinema halls (name)
* **seat** - Physical seats (row, number, hall)
* **screening** - Movie showings (movie, hall, start time)
* **customer** - Users (email, name)
* **reservation** - Booking records (customer, screening, state, created_at)
* **reserved_seat** - Seat allocations (seat, reservation, ticket_type)

### Reservation States

* `RESERVED` - Temporarily held (countdown active)
* `PAID` - Payment confirmed (tickets issued)
* `CANCELED` - Manually cancelled or expired
* `FREE` - Available for booking

## 🔧 Development

### Running Tests

```bash
# Unit tests
./gradlew test

# Integration tests with Testcontainers
./gradlew integrationTest

# Code coverage
./gradlew jacocoTestReport
```

### Code Quality

```bash
# Detekt static analysis
./gradlew detekt

# Generate API documentation
./gradlew dokkaHtml
```

### Database Migrations

Flyway migrations are in `src/main/resources/db/migration/`:

* `V1__init_schema.sql` - Initial database schema
* `V2__test_data.sql` - Sample data (movies, halls, seats)

Create new migration:

```bash
touch src/main/resources/db/migration/V3__add_pricing.sql
```

## 🧪 Testing with Sample Data

The database includes test data:

**Movies:**

- The Matrix (136 min)
- Inception (148 min)

**Halls:**

- Sala 1 (50 seats: A1-A10, B1-B10, C1-C10, D1-D10, E1-E10)
- Sala 2

**Customer:**

- Alice Liddell (alice@example.com)
- Bob Stone (bob@example.com)

**Screenings:**

- The Matrix - Tomorrow in Sala 1
- Inception - In 2 days in Sala 2

## 🔐 Security

* **Input validation** - Jakarta validation on all DTOs
* **SQL injection prevention** - Parameterized queries via Micronaut Data
* **Error handling** - No sensitive data in error responses
* **Audit logging** - All reservation changes logged

## 🤖 CI/CD Pipeline

### Automated Workflows

* **Build & Test** - Runs on every push and PR
* **CodeQL Security Scan** - Weekly vulnerability analysis
* **Dependabot** - Automatic dependency updates
* **Code Coverage** - Reports to Codecov

## 📊 Monitoring & Logging

Logback configuration includes:

* **Console output** - Colored logs for development
* **File rotation** - Daily logs with 30-day retention
* **Error logs** - Separate file for errors (90-day retention)
* **Async appenders** - Non-blocking performance
* **SQL logging** - Optional query debugging

Enable SQL logging:

```xml
<!-- In logback.xml -->
<logger name="org.hibernate.SQL" level="DEBUG"/>
```

## 🚧 Roadmap

- [ ] User authentication & authorization
- [ ] Price calculation per ticket type
- [ ] Email notifications for reservations
- [ ] Admin panel for managing movies/halls
- [ ] Seat selection preferences (aisle, front, back)
- [ ] Group booking optimization
- [ ] WebSocket for real-time availability
- [ ] QR code ticket generation
- [ ] Refund handling for cancellations
- [ ] Analytics dashboard

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/ticket-pricing`)
3. Write tests for new functionality
4. Ensure code passes Detekt checks
5. Submit a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏗️ Built With

* [Kotlin](https://kotlinlang.org/) - Modern JVM language
* [Micronaut](https://micronaut.io/) - Lightweight framework
* [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Async programming
* [PostgresSQL](https://www.postgresql.org/) - Relational database
* [Flyway](https://flywaydb.org/) - Database migrations
* [Gradle](https://gradle.org/) - Build automation
* [Logback](https://logback.qos.ch/) - Logging framework

## 📋 Versioning

We use [Semantic Versioning](http://semver.org/) for versioning.

## 👨‍💻 Authors

* **Rafał Kociniewski** - [rkociniewski](https://github.com/rkociniewski)

See also the list of [contributors](https://github.com/rkociniewski/csrs/contributors) who participated in this project.

## 🙏 Acknowledgments

* Built as a learning project for Kotlin Coroutines and Micronaut
* Inspired by real-world cinema booking systems
* Thanks to the Kotlin and Micronaut communities

## 📞 Support

* **Issues**: [GitHub Issues](https://github.com/rkociniewski/csrs/issues)
* **Discussions**: [GitHub Discussions](https://github.com/rkociniewski/csrs/discussions)

---

Made with ❤️ and 🙏 by [Rafał Kociniewski](https://github.com/rkociniewski)
