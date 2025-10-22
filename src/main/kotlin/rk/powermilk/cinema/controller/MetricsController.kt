package rk.powermilk.cinema.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rk.powermilk.cinema.constant.Numbers
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.metrics.CinemaStats
import rk.powermilk.cinema.metrics.HealthCheck
import rk.powermilk.cinema.repository.ReservationRepository
import rk.powermilk.cinema.repository.ReservedSeatRepository
import rk.powermilk.cinema.repository.ScreeningRepository
import rk.powermilk.cinema.repository.SeatRepository
import java.time.LocalDateTime

@Tag(
    name = "Statistics & Health",
    description = """
        Endpoints for monitoring cinema statistics and application health.

        These endpoints provide business-level insights that complement the technical metrics
        exposed via the /prometheus endpoint. Use them for dashboards, monitoring, and
        operational insights.
    """
)
@Controller("/api/stats")
class MetricsController(
    private val reservationRepository: ReservationRepository,
    private val screeningRepository: ScreeningRepository,
    private val seatRepository: SeatRepository,
    private val reservedSeatRepository: ReservedSeatRepository
) {

    @Operation(
        summary = "Get overall cinema statistics",
        description = """
            Retrieves comprehensive business-level statistics about the cinema operations.

            This endpoint provides a snapshot of:

            **Reservation Metrics:**
            - Total number of all reservations ever created
            - Active reservations (currently in RESERVED state)
            - Paid reservations (successfully completed bookings)
            - Cancelled reservations (expired or manually cancelled)

            **Capacity Metrics:**
            - Total number of screenings
            - Total seat capacity across all halls
            - Currently reserved seats
            - **Occupancy rate** - percentage of seats currently reserved

            **Use Cases:**
            - Dashboard displays
            - Business analytics
            - Capacity planning
            - Performance monitoring

            **Note:** This endpoint performs multiple database queries and may have
            slightly higher response time. Consider caching results for frequently
            accessed dashboards.

            **Example Response:**
            ```json
            {
              "totalReservations": 150,
              "activeReservations": 23,
              "paidReservations": 98,
              "cancelledReservations": 29,
              "totalScreenings": 45,
              "totalSeats": 250,
              "totalReservedSeats": 87,
              "occupancyRate": 34.8
            }
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "Cinema statistics retrieved successfully",
            "200",
            content = [Content(schema = Schema(implementation = CinemaStats::class))]
        )
    )
    @Get
    suspend fun getOverallStats(): CinemaStats = withContext(Dispatchers.IO) {
        val totalReservations = reservationRepository.count()
        val totalScreenings = screeningRepository.count()
        val totalSeats = seatRepository.count()
        val totalReservedSeats = reservedSeatRepository.count()

        val reservedCount = reservationRepository.findAll().count { it.state == ReservationState.RESERVED }
        val paidCount = reservationRepository.findAll().count { it.state == ReservationState.PAID }
        val cancelledCount = reservationRepository.findAll().count { it.state == ReservationState.CANCELED }

        CinemaStats(
            totalReservations,
            reservedCount.toLong(),
            paidCount.toLong(),
            cancelledCount.toLong(),
            totalScreenings,
            totalSeats,
            totalReservedSeats,
            if (totalSeats > 0) {
                (totalReservedSeats.toDouble() / totalSeats) * Numbers.ONE_HUNDRED
            } else 0.0
        )
    }

    @Operation(
        summary = "Application health check",
        description = """
            Performs a basic health check of the cinema reservation system.

            This endpoint verifies:
            - **Application status** - whether the service is running
            - **Database connectivity** - can the application query the database
            - **Timestamp** - current server time

            **Status Values:**
            - `UP` - Application is healthy and database is accessible
            - `DOWN` - Application or database connection has issues

            **Use Cases:**
            - Load balancer health checks
            - Monitoring systems (Nagios, Prometheus, etc.)
            - Kubernetes liveness/readiness probes
            - Quick operational status verification

            **Important:** This is a simplified health check. For comprehensive monitoring,
            use the `/health` endpoint provided by Micronaut or the `/prometheus` endpoint
            for detailed metrics.

            **Example Response (Healthy):**
            ```json
            {
              "status": "UP",
              "database": "Connected",
              "timestamp": "2025-10-16T14:30:00"
            }
            ```

            **Example Response (Unhealthy):**
            ```json
            {
              "status": "DOWN",
              "database": "Error: Connection timeout",
              "timestamp": "2025-10-16T14:30:00"
            }
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "Health check completed (check response body for actual status)",
            "200",
            content = [Content(schema = Schema(implementation = HealthCheck::class))]
        )
    )
    @Suppress("TooGenericExceptionCaught")
    @Get("/health")
    suspend fun getHealthCheck(): HealthCheck = withContext(Dispatchers.IO) {
        try {
            // Simple check - can we query the database?
            reservationRepository.count()

            HealthCheck("UP", "Connected", LocalDateTime.now())
        } catch (e: Exception) {
            HealthCheck("DOWN", "Error: ${e.message}", LocalDateTime.now())
        }
    }
}
