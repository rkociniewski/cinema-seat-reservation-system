package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import rk.powermilk.cinema.model.Seat

@Serdeable
@Introspected
data class SeatAvailabilityDTO(
    @field:Positive(message = "Seat ID must be positive")
    val id: Long,

    @field:NotBlank(message = "Row cannot be blank")
    @field:Size(min = 1, max = 10, message = "Row must be between 1 and 10 characters")
    val row: String,

    @field:Positive(message = "Seat number must be positive")
    val number: Int,

    @field:NotNull(message = "Available status cannot be null")
    val available: Boolean
) {
    companion object {
        fun fromAvailable(seat: Seat) = SeatAvailabilityDTO(seat.id, seat.row, seat.number, true)

        fun fromTaken(seat: Seat) = SeatAvailabilityDTO(seat.id, seat.row, seat.number, false)
    }
}
