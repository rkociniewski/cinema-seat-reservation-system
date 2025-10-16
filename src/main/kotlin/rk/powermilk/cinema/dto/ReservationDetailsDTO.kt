package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.model.Reservation
import rk.powermilk.cinema.model.ReservedSeat
import java.time.LocalDateTime

@Schema(
    description = """
        Request payload for creating a new cinema reservation.

        This DTO represents the complete information needed to reserve seats for a screening.
        All fields are required and validated.

        **Validation Rules:**
        - Must have at least 1 seat selected
        - Maximum 20 seats per reservation
        - All seat IDs must be positive numbers
        - All seat IDs must correspond to existing seats in the screening's hall
        - All selected seats must be available (not already reserved)

        **Transaction Behavior:**
        The reservation is atomic - either ALL seats are reserved successfully,
        or NONE are reserved (rollback on any failure).
    """
)
@Introspected
data class ReservationDetailsDTO(
    @field:Schema(
        description = "Unique identifier of the reservation",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    @field:Positive(message = "Reservation ID must be positive")
    val id: Long,

    @field:Schema(
        description = "ID of the screening this reservation is for",
        example = "1",
        required = true
    )
    @field:Positive(message = "Screening ID must be positive")
    val screeningId: Long,

    @field:Schema(
        description = "Title of the movie being shown",
        example = "The Matrix",
        required = true
    )
    @field:NotBlank(message = "Movie title cannot be blank")
    val movieTitle: String,

    @field:Schema(
        description = "Name of the cinema hall",
        example = "Sala 1",
        required = true
    )
    @field:NotBlank(message = "Hall name cannot be blank")
    val hallName: String,

    @field:Schema(
        description = "Date and time when the screening starts",
        example = "2025-10-17T19:00:00",
        required = true
    )
    @field:NotNull(message = "Start time cannot be null")
    val startTime: LocalDateTime,

    @field:Schema(
        description = "Full name of the customer who made the reservation",
        example = "Alice Liddell",
        required = true
    )
    @field:NotBlank(message = "Customer name cannot be blank")
    val customerName: String,

    @field:Schema(
        description = "Email address of the customer",
        example = "alice@example.com",
        required = true,
        format = "email"
    )
    @field:NotBlank(message = "Customer email cannot be blank")
    @field:Email(message = "Customer email must be valid")
    val customerEmail: String,

    @field:Schema(
        description = """
            List of all seats reserved in this booking.
            Each seat includes its location (row, number) and ticket type.
        """,
        required = true,
    )
    @field:NotEmpty(message = "Reserved seats cannot be empty")
    val seats: List<ReservedSeatDTO>,

    @field:Schema(
        description = """
            Current state of the reservation.

            **Possible values:**
            - RESERVED: Pending payment, will expire if not paid
            - PAID: Payment confirmed, tickets issued
            - CANCELED: Reservation was cancelled or expired
        """,
        required = true,
        implementation = ReservationState::class
    )
    @field:NotNull(message = "Reservation state cannot be null")
    val state: ReservationState,

    @field:Schema(
        description = "Timestamp when the reservation was created",
        example = "2025-10-16T14:30:00",
        required = true
    )
    @field:NotNull(message = "Created at cannot be null")
    val createdAt: LocalDateTime,

    @field:Schema(
        description = """
            Expiration timestamp for RESERVED reservations.

            **Behavior:**
            - Present: Only when state is RESERVED
            - Null: For PAID or CANCELED reservations

            After this time, unpaid reservations will be automatically canceled
            and seats will become available again.

            **Default timeout:** 15 minutes from creation (configurable)
        """,
        example = "2025-10-16T14:45:00",
        nullable = true
    )
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
