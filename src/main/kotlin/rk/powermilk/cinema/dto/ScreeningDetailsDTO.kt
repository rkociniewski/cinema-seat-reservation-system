package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.model.Screening
import java.time.LocalDateTime

@Schema(
    description = """
        Detailed information about a specific screening.

        Extends the basic screening information with real-time seat availability count.
        Used when displaying detailed screening information before seat selection.
    """
)
@Serdeable
@Introspected
data class ScreeningDetailsDTO(
    @field:Schema(
        description = "Unique identifier of the screening",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    @field:Positive(message = "Screening ID must be positive")
    val id: Long,

    @field:Schema(
        description = "Complete movie information (may be null for special events)",
        implementation = MovieDTO::class,
        nullable = true
    )
    val movie: MovieDTO?,

    @field:Schema(
        description = "Name of the hall where screening takes place",
        example = "Sala 1",
        required = true
    )
    @field:NotBlank(message = "Hall name cannot be blank")
    val hallName: String,

    @field:Schema(
        description = "Date and time when the screening starts",
        example = "2025-10-17T19:00:00",
        required = true
    )
    @field:NotNull(message = "Start time cannot be null")
    val startTime: LocalDateTime,

    @field:Schema(
        description = """
            Number of seats currently available for reservation.

            This is calculated in real-time and represents seats that are:
            - Not reserved in any active (RESERVED or PAID) reservation

            **Note:** This number can change between viewing and attempting
            to reserve, as other users may book seats concurrently.
        """,
        example = "47",
        required = true,
        minimum = "0"
    )
    @field:Min(value = 0, message = "Available seats count cannot be negative")
    val availableSeatsCount: Int
) {
    companion object {
        fun from(screening: Screening, availableSeatsCount: Int) = ScreeningDetailsDTO(
            id = screening.id,
            movie = screening.movie?.let { MovieDTO.from(it) },
            hallName = screening.hall?.name ?: "Unknown",
            startTime = screening.startTime,
            availableSeatsCount = availableSeatsCount
        )
    }
}
