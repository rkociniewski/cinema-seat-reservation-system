package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import rk.powermilk.cinema.enums.TicketType

@Serdeable
@Introspected
data class CreateReservationDTO(
    val customerId: Long,
    val screeningId: Long,
    val seats: Map<Long, TicketType> // seatId -> ticketType
)
