package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import rk.powermilk.cinema.enums.TicketType
import rk.powermilk.cinema.model.ReservedSeat

@Serdeable
@Introspected
data class ReservedSeatDTO(
    val seatId: Long,
    val row: String,
    val number: Int,
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
