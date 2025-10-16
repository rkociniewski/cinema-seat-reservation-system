package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.model.Screening
import java.time.LocalDateTime

@Serdeable
@Introspected
data class ScreeningDetailsDTO(
    @field:Positive(message = "Screening ID must be positive")
    val id: Long,

    val movie: MovieDTO?,

    @field:NotBlank(message = "Hall name cannot be blank")
    val hallName: String,

    @field:NotNull(message = "Start time cannot be null")
    val startTime: LocalDateTime,

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
