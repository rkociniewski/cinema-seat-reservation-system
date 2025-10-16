package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.enums.TicketType
import rk.powermilk.cinema.model.ReservedSeat

@Schema(
    description = """
        Information about a single reserved seat within a reservation.

        Contains the seat's physical location (row and number) and the
        type of ticket that was purchased for this seat.
    """
)
@Serdeable
@Introspected
data class ReservedSeatDTO(
    @field:Schema(
        description = "Unique identifier of the seat",
        example = "15",
        required = true
    )
    @field:Positive(message = "Seat ID must be positive")
    val seatId: Long,

    @field:Schema(
        description = "Row identifier (typically a letter like A, B, C)",
        example = "C",
        required = true
    )
    @field:NotBlank(message = "Row cannot be blank")
    val row: String,

    @field:Schema(
        description = "Seat number within the row",
        example = "5",
        required = true,
        minimum = "1"
    )
    @field:Positive(message = "Seat number must be positive")
    val number: Int,

    @field:Schema(
        description = "Type of ticket purchased for this seat",
        required = true,
        implementation = TicketType::class
    )
    @field:NotNull(message = "Ticket type cannot be null")
    val ticketType: TicketType
) {
    companion object {
        fun from(reservedSeat: ReservedSeat) = ReservedSeatDTO(
            reservedSeat.seat.id,
            reservedSeat.seat.row,
            reservedSeat.seat.number,
            reservedSeat.ticketType
        )
    }
}
