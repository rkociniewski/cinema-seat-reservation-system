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
import rk.powermilk.cinema.dto.ScreeningDetailsDTO
import rk.powermilk.cinema.dto.ScreeningListDTO
import rk.powermilk.cinema.dto.SeatAvailabilityDTO
import rk.powermilk.cinema.model.error.ErrorResponse
import rk.powermilk.cinema.service.ScreeningService

/**
 * REST controller for browsing cinema screenings and seat availability.
 *
 * Provides read-only operations for users to explore movie showtimes
 * and check real-time seat availability before making a reservation.
 */
@Tag(name = "Screenings", description = "Operations for browsing movie screenings and checking seat availability")
@Controller("/api/screenings")
class ScreeningController(private val screeningService: ScreeningService) {

    /**
     * Retrieves all available screenings.
     *
     * Returns a simplified list view of all movie showings with basic information
     * (movie title, hall, start time). This is typically used for browsing
     * what's currently showing.
     *
     * @return list of all screenings in the system
     */
    @Operation(
        summary = "Get all screenings",
        description = """
            Retrieves a complete list of all movie screenings available in the cinema.

            Returns a simplified list view with:
            - Screening ID
            - Movie title
            - Hall name
            - Start time
            - Movie duration

            This endpoint is used for browsing all available showtimes across all movies and halls.

            **Example Response:**
            ```json
            [
              {
                "id": 1,
                "movieTitle": "The Matrix",
                "hallName": "Sala 1",
                "startTime": "2025-10-17T19:00:00",
                "durationInMinutes": 136
              }
            ]
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "List of all available screenings",
            "200",
            content = [Content(schema = Schema(implementation = ScreeningListDTO::class))]
        )
    )
    @Get
    suspend fun getAllScreenings() = screeningService.getAllScreenings()

    /**
     * Retrieves detailed information about a specific screening.
     *
     * Includes full movie details, hall information, start time, and the count
     * of available seats. Useful for showing screening details before users
     * proceed to seat selection.
     *
     * @param id the screening ID (must be positive)
     * @return detailed screening information with available seat count
     * @throws NoSuchElementException if screening not found
     */
    @Operation(
        summary = "Get screening details",
        description = """
            Retrieves detailed information about a specific screening.

            Returns complete screening data including:
            - Screening ID
            - Full movie details (title, duration)
            - Hall name
            - Start time
            - **Number of available seats** (real-time calculation)

            This endpoint provides more information than the list view, including the
            current count of available seats. Users typically view this before proceeding
            to seat selection.

            **Example Response:**
            ```json
            {
              "id": 1,
              "movie": {
                "id": 1,
                "title": "The Matrix",
                "durationInMinutes": 136
              },
              "hallName": "Sala 1",
              "startTime": "2025-10-17T19:00:00",
              "availableSeatsCount": 47
            }
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "Screening details retrieved successfully",
            "200",
            content = [Content(schema = Schema(implementation = ScreeningDetailsDTO::class))]
        ),
        ApiResponse(
            "Screening with the specified ID not found",
            "404",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            "Invalid screening ID format (must be positive number)",
            "400",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Get("/{id}")
    suspend fun getScreeningDetails(
        @Parameter(
            description = "Unique identifier of the screening",
            required = true,
            example = "1"
        )
        @Positive(message = "Screening ID must be positive")
        @PathVariable id: Long
    ) = screeningService.getScreeningDetails(id)

    /**
     * Retrieves real-time seat availability for a screening.
     *
     * Returns all seats in the hall with their current availability status.
     * This is the primary endpoint users interact with when selecting seats
     * for their reservation. Availability is calculated in real-time based on
     * existing reservations.
     *
     * @param id the screening ID (must be positive)
     * @return list of all seats with availability status (available: true/false)
     * @throws NoSuchElementException if screening not found
     */
    @Operation(
        summary = "Get seat availability for screening",
        description = """
            Retrieves **real-time seat availability** for a specific screening.

            Returns all seats in the hall with their current availability status:
            - `available: true` - seat can be reserved
            - `available: false` - seat is already taken

            This is the **primary endpoint** users interact with when selecting seats
            for their reservation. The availability is calculated in real-time based on
            existing reservations (both RESERVED and PAID states).

            Seats are organized by row and number for easy display in a seating map UI.

            **Important Notes:**
            - Availability reflects the current state at query time
            - Seats in RESERVED state (unpaid) are shown as taken until they expire
            - Expired reservations are automatically cleaned up every minute

            **Example Response:**
            ```json
            [
              {
                "id": 1,
                "row": "A",
                "number": 1,
                "available": false
              },
              {
                "id": 2,
                "row": "A",
                "number": 2,
                "available": true
              }
            ]
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "Seat availability retrieved successfully",
            "200",
            content = [Content(schema = Schema(implementation = SeatAvailabilityDTO::class))]
        ),
        ApiResponse(
            "Screening with the specified ID not found",
            "404",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            "Invalid screening ID format (must be positive number)",
            "400",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Get("/{id}/seats")
    suspend fun getAvailableSeats(
        @Parameter(
            description = "Unique identifier of the screening",
            required = true,
            example = "1"
        )
        @Positive(message = "Screening ID must be positive")
        @PathVariable id: Long
    ) = screeningService.getAvailableSeatsForScreening(id)

    /**
     * Retrieves all screenings for a specific movie.
     *
     * Useful for showing all available showtimes when a user has selected
     * a movie they want to watch. Returns screenings across all halls and times.
     *
     * @param movieId the movie ID (must be positive)
     * @return list of screenings for the specified movie
     */
    @Operation(
        summary = "Get screenings by movie",
        description = """
            Retrieves all screenings for a specific movie.

            Returns a list of all showtimes when a specific movie is playing,
            across all halls and time slots. This is useful when a user has
            selected a movie and wants to see all available screening times.

            The response includes:
            - Screening IDs
            - Hall names
            - Start times
            - Movie duration

            **Use Case:**
            After a user selects "The Matrix" from the movie list, this endpoint
            shows all available showtimes for that movie.

            **Example Response:**
            ```json
            [
              {
                "id": 1,
                "movieTitle": "The Matrix",
                "hallName": "Sala 1",
                "startTime": "2025-10-17T19:00:00",
                "durationInMinutes": 136
              },
              {
                "id": 5,
                "movieTitle": "The Matrix",
                "hallName": "Sala 2",
                "startTime": "2025-10-17T21:30:00",
                "durationInMinutes": 136
              }
            ]
            ```
        """
    )
    @ApiResponses(
        ApiResponse(
            "List of screenings for the specified movie (may be empty if no screenings found)",
            "200",
            content = [Content(schema = Schema(implementation = ScreeningListDTO::class))]
        ),
        ApiResponse(
            "Invalid movie ID format (must be positive number)",
            "400",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Get("/movie/{movieId}")
    suspend fun getScreeningsByMovie(
        @Parameter(
            description = "Unique identifier of the movie",
            required = true,
            example = "1"
        )
        @Positive(message = "Movie ID must be positive")
        @PathVariable movieId: Long
    ) = screeningService.getScreeningsByMovie(movieId)
}
