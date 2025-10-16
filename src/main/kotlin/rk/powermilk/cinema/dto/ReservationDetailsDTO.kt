package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.model.Reservation
import rk.powermilk.cinema.model.ReservedSeat
import java.time.LocalDateTime

@Serdeable
@Introspected
data class ReservationDetailsDTO(
    val id: Long,
    val screeningId: Long,
    val movieTitle: String,
    val hallName: String,
    val startTime: LocalDateTime,
    val customerName: String,
    val customerEmail: String,
    val seats: List<ReservedSeatDTO>,
    val state: ReservationState,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime?
) {
    companion object {
        fun from(reservation: Reservation, reservedSeats: List<ReservedSeat>, timeoutMinutes: Int) =
            ReservationDetailsDTO(
                reservation.id,
                reservation.screening.id,
                reservation.screening.movie?.title ?: "Unknown",
                reservation.screening.hall?.name ?: "Unknown",
                reservation.screening.startTime,
                reservation.customer.name,
                reservation.customer.email,
                reservedSeats.map { ReservedSeatDTO.from(it) },
                reservation.state,
                reservation.createdAt,
                if (reservation.state == ReservationState.RESERVED) {
                    reservation.createdAt.plusMinutes(timeoutMinutes.toLong())
                } else null
            )

        // Simplified version without reserved seats
        fun from(reservation: Reservation) = ReservationDetailsDTO(
            reservation.id,
            reservation.screening.id,
            reservation.screening.movie?.title ?: "Unknown",
            reservation.screening.hall?.name ?: "Unknown",
            reservation.screening.startTime,
            reservation.customer.name,
            reservation.customer.email,
            emptyList(),
            reservation.state,
            reservation.createdAt,
            null
        )
    }
}
