package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import rk.powermilk.cinema.enums.TicketType

@Serdeable
@Introspected
data class CreateReservationDTO(
    @field:NotNull(message = "Customer ID cannot be null")
    @field:Positive(message = "Customer ID must be positive")
    val customerId: Long,

    @field:NotNull(message = "Screening ID cannot be null")
    @field:Positive(message = "Screening ID must be positive")
    val screeningId: Long,

    @field:NotEmpty(message = "At least one seat must be selected")
    @field:Size(min = 1, max = 20, message = "Can reserve between 1 and 20 seats")
    val seats: Map<
        @Positive(message = "Seat ID must be positive") Long,
        @NotNull(message = "Ticket type cannot be null") TicketType
        >
) {
    init {
        require(seats.isNotEmpty()) { "At least one seat must be selected" }
        require(seats.size <= 20) { "Cannot reserve more than 20 seats in one reservation" }
    }
}
