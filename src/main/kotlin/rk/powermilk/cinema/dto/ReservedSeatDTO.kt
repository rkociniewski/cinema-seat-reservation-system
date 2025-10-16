package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.enums.TicketType
import rk.powermilk.cinema.model.ReservedSeat

@Serdeable
@Introspected
data class ReservedSeatDTO(
    @field:Positive(message = "Seat ID must be positive")
    val seatId: Long,

    @field:NotBlank(message = "Row cannot be blank")
    val row: String,

    @field:Positive(message = "Seat number must be positive")
    val number: Int,

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
