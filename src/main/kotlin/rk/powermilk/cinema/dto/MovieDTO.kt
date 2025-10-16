package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import rk.powermilk.cinema.model.Movie

// Movie DTOs
@Serdeable
@Introspected
data class MovieDTO(
    val id: Long,
    val title: String,
    val durationInMinutes: Int
) {
    companion object {
        fun from(movie: Movie) = MovieDTO(movie.id, movie.title, movie.durationInMinutes)
    }
}
