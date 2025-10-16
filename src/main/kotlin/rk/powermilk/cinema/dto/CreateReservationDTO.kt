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
    @field:Schema(
        description = "ID of the customer making the reservation",
        example = "1",
        required = true,
        minimum = "1"
    )
    @field:NotNull(message = "Customer ID cannot be null")
    @field:Positive(message = "Customer ID must be positive")
    val customerId: Long,

    @field:Schema(
        description = "ID of the screening to reserve seats for",
        example = "1",
        required = true,
        minimum = "1"
    )
    @field:NotNull(message = "Screening ID cannot be null")
    @field:Positive(message = "Screening ID must be positive")
    val screeningId: Long,

    @field:Schema(
        description = """
            Map of seat IDs to their ticket types.

            **Key:** Seat ID (must be positive integer)
            **Value:** Ticket type (STANDARD, CHILD_DISCOUNT, or SENIOR_DISCOUNT)

            This structure allows you to assign different ticket types to different
            seats in the same reservation (e.g., family booking with mixed ticket types).

            **Example:** A family reserves 3 seats:
            - Seat 5 with CHILD_DISCOUNT
            - Seat 6 with STANDARD
            - Seat 7 with SENIOR_DISCOUNT
        """,
        example = """{"5": "CHILD_DISCOUNT", "6": "STANDARD", "7": "SENIOR_DISCOUNT"}""",
        required = true,
        minProperties = 1,
        maxProperties = 20
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
