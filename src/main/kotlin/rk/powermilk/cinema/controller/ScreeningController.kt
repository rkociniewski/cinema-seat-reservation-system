package rk.powermilk.cinema.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.service.ScreeningService

/**
 * REST controller for browsing cinema screenings and seat availability.
 *
 * Provides read-only operations for users to explore movie showtimes
 * and check real-time seat availability before making a reservation.
 */
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
    @Get("/{id}")
    suspend fun getScreeningDetails(@Positive(message = "Screening ID must be positive") @PathVariable id: Long) =
        screeningService.getScreeningDetails(id)

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
    @Get("/{id}/seats")
    suspend fun getAvailableSeats(@Positive(message = "Screening ID must be positive") @PathVariable id: Long) =
        screeningService.getAvailableSeatsForScreening(id)

    /**
     * Retrieves all screenings for a specific movie.
     *
     * Useful for showing all available showtimes when a user has selected
     * a movie they want to watch. Returns screenings across all halls and times.
     *
     * @param movieId the movie ID (must be positive)
     * @return list of screenings for the specified movie
     */
    @Get("/movie/{movieId}")
    suspend fun getScreeningsByMovie(@Positive(message = "Movie ID must be positive") @PathVariable movieId: Long) =
        screeningService.getScreeningsByMovie(movieId)
}
