package rk.powermilk.cinema.metrics

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class CinemaStats(
    val totalReservations: Long,
    val activeReservations: Long,
    val paidReservations: Long,
    val cancelledReservations: Long,
    val totalScreenings: Long,
    val totalSeats: Long,
    val totalReservedSeats: Long,
    val occupancyRate: Double
)
