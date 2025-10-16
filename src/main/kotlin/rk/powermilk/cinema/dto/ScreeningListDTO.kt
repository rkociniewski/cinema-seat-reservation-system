package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import rk.powermilk.cinema.model.Screening
import java.time.LocalDateTime

@Serdeable
@Introspected
data class ScreeningListDTO(
    val id: Long,
    val movieTitle: String,
    val hallName: String,
    val startTime: LocalDateTime,
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
