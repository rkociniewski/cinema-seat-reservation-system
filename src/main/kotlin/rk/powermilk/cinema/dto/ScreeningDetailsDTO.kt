package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import rk.powermilk.cinema.model.Screening
import java.time.LocalDateTime

@Serdeable
@Introspected
data class ScreeningDetailsDTO(
    val id: Long,
    val movie: MovieDTO?,
    val hallName: String,
    val startTime: LocalDateTime,
    val availableSeatsCount: Int
) {
    companion object {
        fun from(screening: Screening, availableSeatsCount: Int) = ScreeningDetailsDTO(
            screening.id,
            screening.movie?.let { MovieDTO.from(it) },
            screening.hall?.name ?: "Unknown",
            screening.startTime,
            availableSeatsCount
        )
    }
}
