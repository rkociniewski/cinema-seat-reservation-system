package rk.powermilk.cinema.task

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import rk.powermilk.cinema.service.ReservationService

@Suppress("TooGenericExceptionCaught")
@Singleton
class ReservationExpirationTask(
    private val reservationService: ReservationService
) {
    private val logger = LoggerFactory.getLogger(ReservationExpirationTask::class.java)

    /**
     * Runs every minute to expire old reservations
     * Cron: every minute at second 0
     */
    @Scheduled(cron = "0 * * * * *")
    fun expireReservations() = runBlocking {
        logger.debug("Running reservation expiration task...")
        try {
            reservationService.expireOldReservations()
            logger.debug("Reservation expiration task completed")
        } catch (e: Exception) {
            logger.error("Error during reservation expiration task", e)
        }
    }
}
