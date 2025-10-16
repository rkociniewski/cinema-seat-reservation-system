package rk.powermilk.cinema.service

import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import rk.powermilk.cinema.dto.ScreeningDetailsDTO
import rk.powermilk.cinema.dto.ScreeningListDTO
import rk.powermilk.cinema.dto.SeatAvailabilityDTO
import rk.powermilk.cinema.model.Seat
import rk.powermilk.cinema.repository.ReservedSeatRepository
import rk.powermilk.cinema.repository.ScreeningRepository
import rk.powermilk.cinema.repository.SeatRepository

/**
 * Service for managing cinema screenings and seat availability.
 *
 * Provides read-only operations for browsing movies showings and checking
 * real-time seat availability. All methods use coroutines for non-blocking I/O
 * and parallel data fetching where applicable.
 */
@Singleton
class ScreeningService(
    private val screeningRepository: ScreeningRepository,
    private val seatRepository: SeatRepository,
    private val reservedSeatRepository: ReservedSeatRepository
) {

    /**
     * Retrieves all available screenings in the system.
     *
     * Returns a simplified list view with movie title, hall name, and start time.
     * Useful for browsing available showings without detailed information.
     *
     * @return list of all screenings as [ScreeningListDTO]
     */
    @ReadOnly
    suspend fun getAllScreenings(): List<ScreeningListDTO> = withContext(Dispatchers.IO) {
        screeningRepository.findAll().map { ScreeningListDTO.from(it) }
    }

    /**
     * Retrieves detailed information about a specific screening.
     *
     * Includes movie details, hall name, start time, and the count of available seats.
     * Performs parallel fetching of screening data and seat availability for better performance.
     *
     * @param screeningId the unique identifier of the screening
     * @return detailed screening information including available seats count
     * @throws NoSuchElementException if screening with given ID does not exist
     */
    @ReadOnly
    suspend fun getScreeningDetails(screeningId: Long): ScreeningDetailsDTO = withContext(Dispatchers.IO) {
        val screening = screeningRepository.findById(screeningId)
            .orElseThrow { NoSuchElementException("Screening not found: $screeningId") }

        val availableSeats = getAvailableSeats(screeningId, screening.hall!!.id)

        ScreeningDetailsDTO.from(screening, availableSeats.size)
    }

    /**
     * Retrieves seat availability status for a specific screening.
     *
     * Returns all seats in the hall with their availability status.
     * Seats are marked as available (true) or taken (false) based on existing reservations.
     * Fetches all seats and reserved seats in parallel for optimal performance.
     *
     * This is the primary method users call before creating a reservation to see
     * which seats they can select.
     *
     * @param screeningId the unique identifier of the screening
     * @return list of all seats with their current availability status
     * @throws NoSuchElementException if screening with given ID does not exist
     */
    @ReadOnly
    suspend fun getAvailableSeatsForScreening(screeningId: Long): List<SeatAvailabilityDTO> =
        withContext(Dispatchers.IO) {
            val screening = screeningRepository.findById(screeningId)
                .orElseThrow { NoSuchElementException("Screening not found: $screeningId") }

            coroutineScope {
                val allSeatsDeferred = async { seatRepository.findByHallId(screening.hall!!.id) }
                val reservedSeatsDeferred = async { reservedSeatRepository.findByScreeningId(screeningId) }

                val allSeats = allSeatsDeferred.await()
                val reservedSeats = reservedSeatsDeferred.await()
                val reservedSeatIds = reservedSeats.map { it.seat.id }.toSet()

                allSeats.map { seat ->
                    if (seat.id in reservedSeatIds) {
                        SeatAvailabilityDTO.fromTaken(seat)
                    } else {
                        SeatAvailabilityDTO.fromAvailable(seat)
                    }
                }
            }
        }

    /**
     * Retrieves all screenings for a specific movie.
     *
     * Useful for finding all showtimes when a user has selected a movie they want to watch.
     *
     * @param movieId the unique identifier of the movie
     * @return list of screenings for the specified movie
     */
    @ReadOnly
    suspend fun getScreeningsByMovie(movieId: Long): List<ScreeningListDTO> = withContext(Dispatchers.IO) {
        screeningRepository.findByMovieId(movieId).map { ScreeningListDTO.from(it) }
    }

    /**
     * Internal helper method to get only available (unreserved) seats.
     *
     * Performs parallel fetching of all hall seats and reserved seats,
     * then filters out the reserved ones.
     *
     * @param screeningId the screening to check
     * @param hallId the hall containing the seats
     * @return list of available seats (not reserved)
     */
    private suspend fun getAvailableSeats(screeningId: Long, hallId: Long): List<Seat> = coroutineScope {
        val allSeatsDeferred = async { seatRepository.findByHallId(hallId) }
        val reservedSeatsDeferred = async { reservedSeatRepository.findByScreeningId(screeningId) }

        val allSeats = allSeatsDeferred.await()
        val reservedSeats = reservedSeatsDeferred.await()
        val reservedSeatIds = reservedSeats.map { it.seat.id }.toSet()

        allSeats.filter { it.id !in reservedSeatIds }
    }
}
