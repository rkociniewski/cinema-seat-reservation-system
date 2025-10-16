package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.model.Reservation
import rk.powermilk.cinema.model.ReservedSeat
import java.time.LocalDateTime

@Introspected
data class ReservationDetailsDTO(
    @field:Positive(message = "Reservation ID must be positive")
    val id: Long,

    @field:Positive(message = "Screening ID must be positive")
    val screeningId: Long,

    @field:NotBlank(message = "Movie title cannot be blank")
    val movieTitle: String,

    @field:NotBlank(message = "Hall name cannot be blank")
    val hallName: String,

    @field:NotNull(message = "Start time cannot be null")
    val startTime: LocalDateTime,

    @field:NotBlank(message = "Customer name cannot be blank")
    val customerName: String,

    @field:NotBlank(message = "Customer email cannot be blank")
    @field:Email(message = "Customer email must be valid")
    val customerEmail: String,

    @field:NotEmpty(message = "Reserved seats cannot be empty")
    val seats: List<ReservedSeatDTO>,

    @field:NotNull(message = "Reservation state cannot be null")
    val state: ReservationState,

    @field:NotNull(message = "Created at cannot be null")
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
