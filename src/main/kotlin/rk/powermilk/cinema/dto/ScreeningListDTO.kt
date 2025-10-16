package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.model.Screening
import java.time.LocalDateTime

@Schema(
    description = """
        Simplified screening information for list views.

        Contains essential screening details without seat availability calculations.
        Used for browsing available screenings before selecting one for details.
    """
)
@Serdeable
@Introspected
data class ScreeningListDTO(
    @field:Schema(
        description = "Unique identifier of the screening",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    @field:Positive(message = "Screening ID must be positive")
    val id: Long,

    @field:Schema(
        description = "Title of the movie being shown",
        example = "The Matrix",
        required = true
    )
    @field:NotBlank(message = "Movie title cannot be blank")
    val movieTitle: String,

    @field:Schema(
        description = "Name of the cinema hall",
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
        description = "Duration of the movie in minutes",
        example = "136",
        required = true,
        minimum = "1"
    )
    @field:Positive(message = "Duration must be positive")
    val durationInMinutes: Int
) {
    companion object {
        fun from(screening: Screening) = ScreeningListDTO(
            screening.id,
            screening.movie?.title ?: "Unknown",
            screening.hall?.name ?: "Unknown",
            screening.startTime,
            screening.movie?.durationInMinutes ?: 0
        )
    }
}
