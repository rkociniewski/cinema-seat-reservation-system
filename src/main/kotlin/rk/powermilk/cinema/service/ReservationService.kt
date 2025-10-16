package rk.powermilk.cinema.service

import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import rk.powermilk.cinema.configuration.ReservationConfig
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.enums.TicketType
import rk.powermilk.cinema.model.Reservation
import rk.powermilk.cinema.model.ReservedSeat
import rk.powermilk.cinema.model.Seat
import rk.powermilk.cinema.repository.CustomerRepository
import rk.powermilk.cinema.repository.ReservationRepository
import rk.powermilk.cinema.repository.ReservedSeatRepository
import rk.powermilk.cinema.repository.ScreeningRepository
import rk.powermilk.cinema.repository.SeatRepository
import java.time.Duration
import java.time.LocalDateTime

@Singleton
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val reservedSeatRepository: ReservedSeatRepository,
    private val seatRepository: SeatRepository,
    private val screeningRepository: ScreeningRepository,
    private val customerRepository: CustomerRepository,
    reservationConfig: ReservationConfig
) {
    private val reservationTimeout: Duration = Duration.ofMinutes(reservationConfig.timeoutMinutes.toLong())

    suspend fun createReservation(
        customerId: Long,
        screeningId: Long,
        seatIdToTicketType: Map<Long, TicketType>
    ): Reservation = withContext(Dispatchers.IO) {
        val customer = customerRepository.findById(customerId)
            .orElseThrow { NoSuchElementException("Customer not found: $customerId") }

        val screening = screeningRepository.findById(screeningId)
            .orElseThrow { NoSuchElementException("Screening not found: $screeningId") }

        // Check if seats are available - parallel validation
        coroutineScope {
            seatIdToTicketType.keys.map {
                async {
                    if (reservedSeatRepository.isSeatTaken(it, screeningId)) {
                        throw IllegalArgumentException("Seat $it is already reserved for this screening.")
                    }
                }
            }.awaitAll()
        }

        // Create reservation
        var reservation = Reservation(0L, screening, LocalDateTime.now(), ReservationState.RESERVED, customer)
        reservation = reservationRepository.save(reservation)

        // Save reserved seats
        coroutineScope {
            seatIdToTicketType.map { (seatId, ticketType) ->
                async {
                    val seat = seatRepository.findById(seatId)
                        .orElseThrow { NoSuchElementException("Seat not found: $seatId") }

                    reservedSeatRepository.save(
                        ReservedSeat(
                            id = 0L, // will be generated
                            seat = seat,
                            reservation = reservation,
                            ticketType = ticketType
                        )
                    )
                }
            }.awaitAll()
        }

        reservation
    }

    suspend fun cancelReservation(reservationId: Long): Unit = withContext(Dispatchers.IO) {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { NoSuchElementException("Reservation not found: $reservationId") }

        if (reservation.state == ReservationState.PAID) {
            throw IllegalStateException("Cannot cancel a paid reservation.")
        }

        val updated = reservation.copy(state = ReservationState.CANCELED)
        reservationRepository.update(updated)
    }

    suspend fun confirmPayment(reservationId: Long): Unit = withContext(Dispatchers.IO) {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { NoSuchElementException("Reservation not found: $reservationId") }

        val elapsedTime = Duration.between(reservation.createdAt, LocalDateTime.now())
        if (elapsedTime > reservationTimeout) {
            throw IllegalStateException("Reservation expired.")
        }

        val updated = reservation.copy(state = ReservationState.PAID)
        reservationRepository.update(updated)
    }

    suspend fun getAvailableSeatsForScreening(screeningId: Long): List<Seat> = withContext(Dispatchers.IO) {
        val screening = screeningRepository.findById(screeningId)
            .orElseThrow { NoSuchElementException("Screening not found: $screeningId") }

        coroutineScope {
            val allSeatsDeferred = async { seatRepository.findByHallId(screening.hall!!.id) }
            val reservedSeatsDeferred = async { reservedSeatRepository.findByScreeningId(screeningId) }

            val allSeats = allSeatsDeferred.await()
            val reservedSeats = reservedSeatsDeferred.await()

            val reservedSeatIds = reservedSeats.map { it.seat.id }.toSet()
            allSeats.filter { seat -> seat.id !in reservedSeatIds }
        }
    }

    suspend fun expireOldReservations(): Unit = withContext(Dispatchers.IO) {
        val expirationTime = LocalDateTime.now().minus(reservationTimeout)
        val expiredReservations = reservationRepository.findExpiredReservations(expirationTime)

        coroutineScope {
            expiredReservations.map { reservation ->
                async {
                    val updated = reservation.copy(state = ReservationState.CANCELED)
                    reservationRepository.update(updated)
                }
            }.awaitAll()
        }
    }

    suspend fun getReservationsByCustomer(customerId: Long): List<Reservation> = withContext(Dispatchers.IO) {
        reservationRepository.findByCustomerId(customerId)
    }
}
