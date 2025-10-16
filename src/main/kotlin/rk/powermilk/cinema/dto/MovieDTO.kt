package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import rk.powermilk.cinema.model.Movie

@Serdeable
@Introspected
data class MovieDTO(
    @field:Positive(message = "Movie ID must be positive")
    val id: Long,

    @field:NotBlank(message = "Movie title cannot be blank")
    @field:Size(min = 1, max = 255, message = "Movie title must be between 1 and 255 characters")
    val title: String,

    @field:Positive(message = "Duration must be positive")
    @field:Min(value = 1, message = "Duration must be at least 1 minute")
    val durationInMinutes: Int
) {
    companion object {
        fun from(movie: Movie) = MovieDTO(
            id = movie.id,
            title = movie.title,
            durationInMinutes = movie.durationInMinutes
        )
    }
}
