package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import rk.powermilk.cinema.enums.TicketType

@Serdeable
@Introspected
@Schema(description = "Request to create a new reservation")
data class CreateReservationDTO(
    @field:Schema(description = "Customer ID", example = "1", required = true)
    @field:NotNull(message = "Customer ID cannot be null")
    @field:Positive(message = "Customer ID must be positive")
    val customerId: Long,

    @field:Schema(description = "Screening ID", example = "1", required = true)
    @field:NotNull(message = "Screening ID cannot be null")
    @field:Positive(message = "Screening ID must be positive")
    val screeningId: Long,

    @field:Schema(
        description = "Map of seat IDs to ticket types",
        example = """{"5": "CHILD_DISCOUNT", "6": "STANDARD", "7": "SENIOR_DISCOUNT"}""",
        required = true
    )
    @field:Size(min = 1, max = 20, message = "Can reserve between 1 and 20 seats")
    @field:NotEmpty(message = "At least one seat must be selected")
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
