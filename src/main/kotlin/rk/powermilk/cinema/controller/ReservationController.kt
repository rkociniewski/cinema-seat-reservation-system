package rk.powermilk.cinema.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.dto.CreateReservationDTO
import rk.powermilk.cinema.dto.PaymentConfirmationDTO
import rk.powermilk.cinema.dto.ReservationDetailsDTO
import rk.powermilk.cinema.service.ReservationService

/**
 * REST controller for managing cinema seat reservations.
 *
 * Handles the complete reservation lifecycle: creation, payment confirmation,
 * and cancellation. All request bodies are validated using Jakarta Bean Validation.
 */
@Controller("/api/reservations")
class ReservationController(private val reservationService: ReservationService) {

    /**
     * Creates a new reservation for selected seats.
     *
     * Seats are temporarily held for the configured timeout period (default 15 minutes).
     * All selected seats must be available, otherwise the entire operation fails.
     *
     * @param request validated reservation request with customer, screening, and seat selections
     * @return HTTP 201 Created with reservation details including expiration time
     * @throws IllegalArgumentException if any seat is already taken
     * @throws NoSuchElementException if customer or screening not found
     */
    @Post
    suspend fun createReservation(@Valid @Body request: CreateReservationDTO): HttpResponse<ReservationDetailsDTO> {
        val reservation = reservationService.createReservation(
            customerId = request.customerId,
            screeningId = request.screeningId,
            seatIdToTicketType = request.seats
        )

        return HttpResponse.created(ReservationDetailsDTO.from(reservation))
    }

    /**
     * Retrieves details of a specific reservation.
     *
     * @param id the reservation ID (must be positive)
     * @return reservation details including all reserved seats
     * @throws NoSuchElementException if reservation not found
     */
    @Get("/{id}")
    suspend fun getReservation(@Positive(message = "Reservation ID must be positive") @PathVariable id: Long) =
        reservationService.getReservationDetails(id)

    /**
     * Confirms payment for a reservation within the timeout window.
     *
     * Changes reservation state from RESERVED to PAID. Must be called before
     * the reservation expires, otherwise will fail with IllegalStateException.
     *
     * @param id the reservation ID (must be positive)
     * @param payment optional payment details (method, transaction ID)
     * @return HTTP 200 OK with updated reservation showing PAID state
     * @throws IllegalStateException if reservation expired or already paid
     * @throws NoSuchElementException if reservation not found
     */
    @Post("/{id}/payment")
    suspend fun confirmPayment(
        @Positive(message = "Reservation ID must be positive") @PathVariable id: Long,
        @Valid @Body payment: PaymentConfirmationDTO
    ): HttpResponse<ReservationDetailsDTO> {
        reservationService.confirmPayment(id)
        val updated = reservationService.getReservationDetails(id)
        return HttpResponse.ok(updated)
    }

    /**
     * Cancels a reservation and releases the seats.
     *
     * Only RESERVED reservations can be cancelled. Paid reservations cannot be cancelled
     * (would require separate refund logic).
     *
     * @param id the reservation ID (must be positive)
     * @return HTTP 204 No Content on successful cancellation
     * @throws IllegalStateException if trying to cancel paid reservation
     * @throws NoSuchElementException if reservation not found
     */
    @Delete("/{id}")
    suspend fun cancelReservation(
        @Positive(message = "Reservation ID must be positive") @PathVariable id: Long
    ): HttpResponse<Void> {
        reservationService.cancelReservation(id)
        return HttpResponse.noContent()
    }

    /**
     * Retrieves all reservations for a specific customer.
     *
     * Includes reservations in all states (RESERVED, PAID, CANCELED).
     * Useful for showing customer's booking history.
     *
     * @param customerId the customer ID (must be positive)
     * @return list of all customer's reservations with full details
     */
    @Get("/customer/{customerId}")
    suspend fun getCustomerReservations(
        @Positive(message = "Customer ID must be positive") @PathVariable customerId: Long
    ) = reservationService.getReservationsByCustomerId(customerId)
}
