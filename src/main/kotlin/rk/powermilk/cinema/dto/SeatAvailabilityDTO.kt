package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import rk.powermilk.cinema.model.Seat

@Schema(
    description = """
        Real-time availability status of a seat for a specific screening.

        Used to display seating maps with current availability.
        Each seat shows its physical location and whether it can be reserved.

        **Display Recommendations:**
        - available: true → Show as selectable (e.g., green/white)
        - available: false → Show as taken (e.g., red/gray)
    """
)
@Serdeable
@Introspected
data class SeatAvailabilityDTO(
    @field:Schema(
        description = "Unique identifier of the seat",
        example = "15",
        required = true
    )
    @field:Positive(message = "Seat ID must be positive")
    val id: Long,

    @field:Schema(
        description = "Row identifier (typically a letter)",
        example = "C",
        required = true,
        minLength = 1,
        maxLength = 10
    )
    @field:NotBlank(message = "Row cannot be blank")
    @field:Size(min = 1, max = 10, message = "Row must be between 1 and 10 characters")
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
        description = """
            Availability status of the seat.

            **Values:**
            - true: Seat is available and can be reserved
            - false: Seat is already taken (in RESERVED or PAID state)

            **Important:** Availability is calculated at query time.
            Between checking availability and submitting a reservation,
            another user might reserve the seat (race condition).
            The reservation endpoint will reject the attempt with a 409 Conflict
            if seats become unavailable.
        """,
        example = "true",
        required = true
    )
    @field:NotNull(message = "Available status cannot be null")
    val available: Boolean
) {
    companion object {
        fun fromAvailable(seat: Seat) = SeatAvailabilityDTO(seat.id, seat.row, seat.number, true)
        fun fromTaken(seat: Seat) = SeatAvailabilityDTO(seat.id, seat.row, seat.number, false)
    }
}
