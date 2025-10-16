package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import rk.powermilk.cinema.model.Seat

@Serdeable
@Introspected
data class SeatAvailabilityDTO(
    val id: Long,
    val row: String,
    val number: Int,
    val available: Boolean
) {
    companion object {
        fun fromAvailable(seat: Seat) = SeatAvailabilityDTO(seat.id, seat.row, seat.number, true)
        fun fromTaken(seat: Seat) = SeatAvailabilityDTO(seat.id, seat.row, seat.number, false)
    }
}
