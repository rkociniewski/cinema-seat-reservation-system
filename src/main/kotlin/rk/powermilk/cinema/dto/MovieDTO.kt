package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import rk.powermilk.cinema.model.Movie

@Serdeable
@Introspected
data class MovieDTO(
    @field:Schema(
        description = "Unique identifier of the movie",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    @field:Positive(message = "Movie ID must be positive")
    val id: Long,

    @field:Schema(
        description = "Title of the movie",
        example = "The Matrix",
        required = true,
        minLength = 1,
        maxLength = 255
    )
    @field:NotBlank(message = "Movie title cannot be blank")
    @field:Size(min = 1, max = 255, message = "Movie title must be between 1 and 255 characters")
    val title: String,

    @field:Schema(
        description = "Duration of the movie in minutes",
        example = "136",
        required = true,
        minimum = "1"
    )
    @field:Positive(message = "Duration must be positive")
    @field:Min(value = 1, message = "Duration must be at least 1 minute")
    val durationInMinutes: Int
) {
    companion object {
        fun from(movie: Movie) = MovieDTO(movie.id, movie.title, movie.durationInMinutes)
    }
}
