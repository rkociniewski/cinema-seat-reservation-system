package rk.powermilk.cinema.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.metrics.CinemaStats
import rk.powermilk.cinema.metrics.HealthCheck
import rk.powermilk.cinema.repository.ReservationRepository
import rk.powermilk.cinema.repository.ReservedSeatRepository
import rk.powermilk.cinema.repository.ScreeningRepository
import rk.powermilk.cinema.repository.SeatRepository
import java.time.LocalDateTime

/**
 * Custom endpoint for cinema-specific statistics and metrics.
 *
 * Provides business-level insights that complement the technical metrics
 * exposed via /prometheus endpoint.
 */
@Controller("/api/stats")
class MetricsController(
    private val reservationRepository: ReservationRepository,
    private val screeningRepository: ScreeningRepository,
    private val seatRepository: SeatRepository,
    private val reservedSeatRepository: ReservedSeatRepository
) {

    /**
     * Returns overall cinema statistics.
     */
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
            totalReservations = totalReservations,
            activeReservations = reservedCount.toLong(),
            paidReservations = paidCount.toLong(),
            cancelledReservations = cancelledCount.toLong(),
            totalScreenings = totalScreenings,
            totalSeats = totalSeats,
            totalReservedSeats = totalReservedSeats,
            occupancyRate = if (totalSeats > 0) {
                (totalReservedSeats.toDouble() / totalSeats) * 100
            } else 0.0
        )
    }

    /**
     * Returns health check information.
     */
    @Suppress("TooGenericExceptionCaught")
    @Get("/health")
    suspend fun getHealthCheck(): HealthCheck = withContext(Dispatchers.IO) {
        try {
            // Simple check - can we query the database?
            reservationRepository.count()

            HealthCheck(
                status = "UP",
                database = "Connected",
                timestamp = LocalDateTime.now()
            )
        } catch (e: Exception) {
            HealthCheck(
                status = "DOWN",
                database = "Error: ${e.message}",
                timestamp = java.time.LocalDateTime.now()
            )
        }
    }
}
