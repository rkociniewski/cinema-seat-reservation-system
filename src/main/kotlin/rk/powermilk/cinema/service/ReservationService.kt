package rk.powermilk.cinema.service

import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import rk.powermilk.cinema.configuration.ReservationConfig
import rk.powermilk.cinema.dto.ReservationDetailsDTO
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
    private val metricsService: rk.powermilk.cinema.metrics.MetricsService,
    reservationConfig: ReservationConfig
) {
    private val logger = LoggerFactory.getLogger(ReservationService::class.java)
    private val reservationTimeout: Duration = Duration.ofMinutes(reservationConfig.timeoutMinutes.toLong())

    /**
     * Creates a new reservation with multiple seats.
     * This operation is transactional - either all seats are reserved or none.
     *
     * @throws NoSuchElementException if customer or screening not found
     * @throws IllegalArgumentException if any seat is already taken
     */
    @Transactional
    suspend fun createReservation(
        customerId: Long,
        screeningId: Long,
        seatIdToTicketType: Map<Long, TicketType>
    ): Reservation = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        logger.debug(
            "Creating reservation for customer={}, screening={}, seats={}",
            customerId, screeningId, seatIdToTicketType.keys
        )

        try {
            val customer = customerRepository.findById(customerId)
                .orElseThrow { NoSuchElementException("Customer not found: $customerId") }

            val screening = screeningRepository.findById(screeningId)
                .orElseThrow { NoSuchElementException("Screening not found: $screeningId") }

            // Check if seats are available - parallel validation
            coroutineScope {
                seatIdToTicketType.keys.map { seatId ->
                    async {
                        if (reservedSeatRepository.isSeatTaken(seatId, screeningId)) {
                            metricsService.recordSeatReservationFailed(screeningId)
                            throw IllegalArgumentException("Seat $seatId is already reserved for this screening.")
                        }
                    }
                }.awaitAll()
            }

            // Create reservation
            val reservation = reservationRepository.save(
                Reservation(
                    0L,
                    screening,
                    LocalDateTime.now(),
                    ReservationState.RESERVED,
                    customer
                )
            )
            logger.info("Created reservation id={} for customer={}", reservation.id, customer.email)

            // Save reserved seats - all in same transaction
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

                        // Update ticket type distribution metric
                        metricsService.updateTicketTypeDistribution(ticketType, 1)
                    }
                }.awaitAll()
            }

            logger.info("Reserved {} seats for reservation id={}", seatIdToTicketType.size, reservation.id)

            val duration = System.currentTimeMillis() - startTime
            metricsService.recordReservationCreated(duration, seatIdToTicketType.size)
            metricsService.recordCoroutineExecution("createReservation", duration)

            reservation
        } catch (e: IllegalArgumentException) {
            metricsService.recordSeatReservationFailed(screeningId)
            throw e
        }
    }

    /**
     * Cancels a reservation and releases the seats.
     *
     * @throws NoSuchElementException if reservation not found
     * @throws IllegalStateException if trying to cancel paid reservation
     */
    @Transactional
    suspend fun cancelReservation(reservationId: Long): Unit = withContext(Dispatchers.IO) {
        logger.debug("Cancelling reservation id={}", reservationId)

        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { NoSuchElementException("Reservation not found: $reservationId") }

        if (reservation.state == ReservationState.PAID) {
            throw IllegalStateException("Cannot cancel a paid reservation.")
        }

        if (reservation.state == ReservationState.CANCELED) {
            logger.warn("Reservation id={} is already cancelled", reservationId)
            return@withContext
        }

        val updated = reservation.copy(state = ReservationState.CANCELED)
        reservationRepository.update(updated)

        metricsService.recordReservationCancelled("user_cancelled")

        logger.info("Cancelled reservation id={}, seats are now available", reservationId)
    }

    /**
     * Confirms payment for a reservation within the timeout window.
     *
     * @throws NoSuchElementException if reservation not found
     * @throws IllegalStateException if reservation expired or already paid
     */
    @Transactional
    suspend fun confirmPayment(reservationId: Long): Unit = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        logger.debug("Confirming payment for reservation id={}", reservationId)

        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { NoSuchElementException("Reservation not found: $reservationId") }

        if (reservation.state == ReservationState.PAID) {
            logger.warn("Reservation id={} is already paid", reservationId)
            return@withContext
        }

        if (reservation.state == ReservationState.CANCELED) {
            throw IllegalStateException("Cannot confirm payment for cancelled reservation.")
        }

        val elapsedTime = Duration.between(reservation.createdAt, LocalDateTime.now())
        if (elapsedTime > reservationTimeout) {
            metricsService.recordPaymentTimeout()
            throw IllegalStateException(
                "Reservation expired. Created at ${reservation.createdAt}, " +
                    "timeout was ${reservationTimeout.toMinutes()} minutes."
            )
        }

        val updated = reservation.copy(state = ReservationState.PAID)
        reservationRepository.update(updated)

        val duration = System.currentTimeMillis() - startTime
        metricsService.recordPaymentConfirmed(duration)
        metricsService.recordCoroutineExecution("confirmPayment", duration)

        logger.info(
            "Payment confirmed for reservation id={}, customer={}",
            reservationId, reservation.customer.email
        )
    }

    /**
     * Gets available seats for a screening (parallel fetch).
     */
    suspend fun getAvailableSeatsForScreening(screeningId: Long): List<Seat> = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()

        val screening = screeningRepository.findById(screeningId)
            .orElseThrow { NoSuchElementException("Screening not found: $screeningId") }

        val result = coroutineScope {
            val allSeatsDeferred = async { seatRepository.findByHallId(screening.hall!!.id) }
            val reservedSeatsDeferred = async { reservedSeatRepository.findByScreeningId(screeningId) }

            val allSeats = allSeatsDeferred.await()
            val reservedSeats = reservedSeatsDeferred.await()

            val reservedSeatIds = reservedSeats.map { it.seat.id }.toSet()
            allSeats.filter { seat -> seat.id !in reservedSeatIds }
        }

        val duration = System.currentTimeMillis() - startTime
        val totalSeats = seatRepository.findByHallId(screening.hall!!.id).size
        metricsService.recordSeatAvailabilityCheck(duration, result.size, totalSeats)
        metricsService.recordCoroutineExecution("getAvailableSeats", duration)

        result
    }

    /**
     * Expires old reservations in RESERVED state.
     * This is called by scheduled task and processes multiple reservations.
     */
    @Transactional
    suspend fun expireOldReservations(): Unit = withContext(Dispatchers.IO) {
        val expirationTime = LocalDateTime.now().minus(reservationTimeout)
        val expiredReservations = reservationRepository.findExpiredReservations(expirationTime)

        if (expiredReservations.isEmpty()) {
            logger.debug("No expired reservations found")
            return@withContext
        }

        logger.info("Found {} expired reservations to cancel", expiredReservations.size)

        // Update all expired reservations in parallel within the transaction
        coroutineScope {
            expiredReservations.map { reservation ->
                async {
                    val updated = reservation.copy(state = ReservationState.CANCELED)
                    reservationRepository.update(updated)
                    logger.debug("Expired reservation id={}", reservation.id)
                }
            }.awaitAll()
        }

        metricsService.recordReservationExpired(expiredReservations.size)
        metricsService.recordReservationCancelled("expired")

        logger.info("Successfully cancelled {} expired reservations", expiredReservations.size)
    }

    suspend fun getReservationsByCustomerId(customerId: Long): List<rk.powermilk.cinema.dto.ReservationDetailsDTO> =
        withContext(Dispatchers.IO) {
            val reservations = reservationRepository.findByCustomerId(customerId)

            reservations.map { reservation ->
                val reservedSeats = reservedSeatRepository.findByReservationId(reservation.id)
                ReservationDetailsDTO.from(
                    reservation,
                    reservedSeats,
                    (reservationTimeout.toMinutes()).toInt()
                )
            }
        }

    suspend fun getReservationDetails(reservationId: Long): rk.powermilk.cinema.dto.ReservationDetailsDTO =
        withContext(Dispatchers.IO) {
            val reservation = reservationRepository.findById(reservationId)
                .orElseThrow { NoSuchElementException("Reservation not found: $reservationId") }

            val reservedSeats = reservedSeatRepository.findByReservationId(reservationId)

            ReservationDetailsDTO.from(
                reservation,
                reservedSeats,
                (reservationTimeout.toMinutes()).toInt()
            )
        }
}
