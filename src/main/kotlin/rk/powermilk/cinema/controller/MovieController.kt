package rk.powermilk.cinema.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.dto.MovieDTO
import rk.powermilk.cinema.model.error.ErrorResponse
import rk.powermilk.cinema.service.MovieService

/**
 * REST controller for browsing the movie catalog.
 *
 * Provides read-only access to available movies. This is typically the
 * first step in the reservation flow where users explore what films
 * are currently showing.
 */
@Tag(name = "Movies", description = "Operations for browsing the cinema movie catalog")
@Controller("/api/movies")
class MovieController(private val movieService: MovieService) {

    /**
     * Retrieves all movies in the cinema catalog.
     *
     * Returns a complete list of available films with their basic information
     * (title, duration). This is the entry point for users to browse what's
     * currently showing before selecting a specific screening.
     *
     * @return list of all movies
     */
    @Operation(
        summary = "Get all movies",
        description = """
            Retrieves a complete list of all movies currently available in the cinema catalog.

            This endpoint returns basic movie information including title and duration.
            Use this to display the movie selection interface to users at the beginning
            of the booking flow.

            **Example Response:**
            ```json
            [
              {
                "id": 1,
                "title": "The Matrix",
                "durationInMinutes": 136
              },
              {
                "id": 2,
                "title": "Inception",
                "durationInMinutes": 148
              }
            ]
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "List of all available movies",
            "200",
            content = [Content(schema = Schema(implementation = MovieDTO::class))]
        )
    )
    @Get
    suspend fun getAllMovies() = movieService.getAllMovies()

    /**
     * Retrieves detailed information about a specific movie.
     *
     * Returns complete movie data including title and duration in minutes.
     * Users typically view this when they want more information about a film
     * before checking available screenings.
     *
     * @param id the movie ID (must be positive)
     * @return movie details
     * @throws NoSuchElementException if movie not found
     */
    @Operation(
        summary = "Get movie details",
        description = """
            Retrieves detailed information about a specific movie by its unique identifier.

            Returns complete movie data including:
            - Movie title
            - Duration in minutes
            - Movie ID

            This endpoint is typically used when a user wants to see more information
            about a specific film before checking available screenings.

            **Example Response:**
            ```json
            {
              "id": 1,
              "title": "The Matrix",
              "durationInMinutes": 136
            }
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "Movie details retrieved successfully",
            "200",
            content = [Content(schema = Schema(implementation = MovieDTO::class))]
        ),
        ApiResponse(
            "Movie with the specified ID not found",
            "404",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            "Invalid movie ID format (must be positive number)",
            "400",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Get("/{id}")
    suspend fun getMovie(
        @Parameter(
            description = "Unique identifier of the movie",
            required = true,
            example = "1"
        )
        @Positive(message = "Movie ID must be positive")
        @PathVariable id: Long
    ) = movieService.getMovie(id)
}
