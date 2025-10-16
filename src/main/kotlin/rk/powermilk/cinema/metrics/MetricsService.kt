package rk.powermilk.cinema.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import jakarta.inject.Singleton
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.enums.TicketType
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for recording custom application metrics.
 *
 * Provides methods to track business-specific metrics like reservation counts,
 * seat availability, payment success rates, and operation timings.
 * All metrics are exposed via the /prometheus endpoint.
 */
@Singleton
class MetricsService(private val meterRegistry: MeterRegistry) {

    // Counters
    private val reservationCreatedCounter: Counter = Counter.builder("cinema.reservations.created")
        .description("Total number of reservations created")
        .register(meterRegistry)

    private val reservationCancelledCounter: Counter = Counter.builder("cinema.reservations.cancelled")
        .description("Total number of reservations cancelled")
        .register(meterRegistry)

    private val reservationPaidCounter: Counter = Counter.builder("cinema.reservations.paid")
        .description("Total number of paid reservations")
        .register(meterRegistry)

    private val reservationExpiredCounter: Counter = Counter.builder("cinema.reservations.expired")
        .description("Total number of expired reservations")
        .register(meterRegistry)

    private val seatReservationFailedCounter: Counter = Counter.builder("cinema.seats.reservation.failed")
        .description("Number of failed seat reservations (already taken)")
        .register(meterRegistry)

    private val paymentTimeoutCounter: Counter = Counter.builder("cinema.payments.timeout")
        .description("Number of payment attempts that exceeded timeout")
        .register(meterRegistry)

    // Timers
    private val reservationCreationTimer: Timer = Timer.builder("cinema.reservations.creation.time")
        .description("Time taken to create a reservation")
        .register(meterRegistry)

    private val seatAvailabilityCheckTimer: Timer = Timer.builder("cinema.seats.availability.check.time")
        .description("Time taken to check seat availability")
        .register(meterRegistry)

    private val paymentConfirmationTimer: Timer = Timer.builder("cinema.payments.confirmation.time")
        .description("Time taken to confirm payment")
        .register(meterRegistry)

    // Gauges - using concurrent maps for thread safety
    private val activeReservationsByState = ConcurrentHashMap<ReservationState, Int>()
    private val ticketTypeDistribution = ConcurrentHashMap<TicketType, Int>()

    init {
        // Initialize gauges
        ReservationState.entries.forEach { state ->
            activeReservationsByState[state] = 0
            meterRegistry.gauge(
                "cinema.reservations.active",
                listOf(io.micrometer.core.instrument.Tag.of("state", state.name)),
                activeReservationsByState
            ) { it[state]?.toDouble() ?: 0.0 }
        }

        TicketType.entries.forEach { type ->
            ticketTypeDistribution[type] = 0
            meterRegistry.gauge(
                "cinema.tickets.distribution",
                listOf(io.micrometer.core.instrument.Tag.of("type", type.name)),
                ticketTypeDistribution
            ) { it[type]?.toDouble() ?: 0.0 }
        }
    }

    /**
     * Records a reservation creation event with timing.
     */
    fun recordReservationCreated(durationMillis: Long, seatCount: Int) {
        reservationCreatedCounter.increment()
        reservationCreationTimer.record(durationMillis, java.util.concurrent.TimeUnit.MILLISECONDS)

        meterRegistry.counter(
            "cinema.seats.reserved",
            "count", seatCount.toString()
        ).increment(seatCount.toDouble())
    }

    /**
     * Records a reservation cancellation.
     */
    fun recordReservationCancelled(reason: String = "user_cancelled") {
        reservationCancelledCounter.increment()

        meterRegistry.counter(
            "cinema.reservations.cancelled.by.reason",
            "reason", reason
        ).increment()
    }

    /**
     * Records a successful payment.
     */
    fun recordPaymentConfirmed(durationMillis: Long) {
        reservationPaidCounter.increment()
        paymentConfirmationTimer.record(durationMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
    }

    /**
     * Records an expired reservation.
     */
    fun recordReservationExpired(count: Int = 1) {
        reservationExpiredCounter.increment(count.toDouble())
    }

    /**
     * Records a failed seat reservation attempt.
     */
    fun recordSeatReservationFailed(screeningId: Long) {
        seatReservationFailedCounter.increment()

        meterRegistry.counter(
            "cinema.seats.reservation.failed.by.screening",
            "screening_id", screeningId.toString()
        ).increment()
    }

    /**
     * Records a payment timeout.
     */
    fun recordPaymentTimeout() {
        paymentTimeoutCounter.increment()
    }

    /**
     * Records seat availability check timing.
     */
    fun recordSeatAvailabilityCheck(durationMillis: Long, availableCount: Int, totalCount: Int) {
        seatAvailabilityCheckTimer.record(durationMillis, java.util.concurrent.TimeUnit.MILLISECONDS)

        val occupancyRate = if (totalCount > 0) {
            ((totalCount - availableCount).toDouble() / totalCount) * 100
        } else 0.0

        meterRegistry.gauge(
            "cinema.seats.occupancy.rate",
            occupancyRate
        )
    }

    /**
     * Updates active reservations count by state.
     */
    fun updateActiveReservations(state: ReservationState, count: Int) {
        activeReservationsByState[state] = count
    }

    /**
     * Updates ticket type distribution.
     */
    fun updateTicketTypeDistribution(type: TicketType, count: Int) {
        ticketTypeDistribution[type] = count
    }

    /**
     * Records a database query execution time.
     */
    fun recordDatabaseQuery(queryName: String, durationMillis: Long) {
        meterRegistry.timer(
            "cinema.database.query.time",
            "query", queryName
        ).record(durationMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
    }

    /**
     * Records a coroutine execution time.
     */
    fun recordCoroutineExecution(operationName: String, durationMillis: Long) {
        meterRegistry.timer(
            "cinema.coroutine.execution.time",
            "operation", operationName
        ).record(durationMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
    }
}
