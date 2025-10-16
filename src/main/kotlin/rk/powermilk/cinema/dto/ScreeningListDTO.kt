package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.model.Screening
import java.time.LocalDateTime

@Serdeable
@Introspected
data class ScreeningListDTO(
    @field:Positive(message = "Screening ID must be positive")
    val id: Long,

    @field:NotBlank(message = "Movie title cannot be blank")
    val movieTitle: String,

    @field:NotBlank(message = "Hall name cannot be blank")
    val hallName: String,

    @field:NotNull(message = "Start time cannot be null")
    val startTime: LocalDateTime,

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
