package rk.powermilk.cinema.service

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

@Singleton
class ScreeningService(
    private val screeningRepository: ScreeningRepository,
    private val seatRepository: SeatRepository,
    private val reservedSeatRepository: ReservedSeatRepository
) {

    suspend fun getAllScreenings(): List<ScreeningListDTO> = withContext(Dispatchers.IO) {
        screeningRepository.findAll()
            .map { ScreeningListDTO.from(it) }
    }

    suspend fun getScreeningDetails(screeningId: Long): ScreeningDetailsDTO = withContext(Dispatchers.IO) {
        val screening = screeningRepository.findById(screeningId)
            .orElseThrow { NoSuchElementException("Screening not found: $screeningId") }

        val availableSeats = getAvailableSeats(screeningId, screening.hall!!.id)

        ScreeningDetailsDTO.from(screening, availableSeats.size)
    }

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

    suspend fun getScreeningsByMovie(movieId: Long): List<ScreeningListDTO> = withContext(Dispatchers.IO) {
        screeningRepository.findByMovieId(movieId)
            .map { ScreeningListDTO.from(it) }
    }

    private suspend fun getAvailableSeats(screeningId: Long, hallId: Long): List<Seat> = coroutineScope {
        val allSeatsDeferred = async { seatRepository.findByHallId(hallId) }
        val reservedSeatsDeferred = async { reservedSeatRepository.findByScreeningId(screeningId) }

        val allSeats = allSeatsDeferred.await()
        val reservedSeats = reservedSeatsDeferred.await()
        val reservedSeatIds = reservedSeats.map { it.seat.id }.toSet()

        allSeats.filter { it.id !in reservedSeatIds }
    }
}
