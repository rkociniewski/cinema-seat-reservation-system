package rk.powermilk.cinema.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import rk.powermilk.cinema.dto.CreateReservationDTO
import rk.powermilk.cinema.dto.ReservationDetailsDTO
import rk.powermilk.cinema.model.error.ErrorResponse
import rk.powermilk.cinema.model.error.ValidationErrorResponse
import rk.powermilk.cinema.service.ReservationService

@Tag(name = "Reservations", description = "Operations for managing cinema seat reservations")
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
    @Operation(
        summary = "Create new reservation",
        description = """
            Creates a new reservation for selected seats. Seats are temporarily held for the configured
            timeout period (default 15 minutes). All selected seats must be available.

            **Ticket Types:**
            - STANDARD: Regular price ticket
            - CHILD_DISCOUNT: Discounted ticket for children
            - SENIOR_DISCOUNT: Discounted ticket for seniors
        """
    )
    @ApiResponses(
        ApiResponse(
            "Reservation created successfully",
            "201",
            content = [Content(schema = Schema(implementation = ReservationDetailsDTO::class))]
        ),
        ApiResponse(
            "Invalid request data",
            "400",
            content = [Content(schema = Schema(implementation = ValidationErrorResponse::class))]
        ),
        ApiResponse(
            "Customer or screening not found",
            "404",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            "One or more seats already taken",
            "409",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Post
    suspend fun createReservation(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Reservation details with customer, screening and seat selections",
            required = true,
            content = [Content(schema = Schema(implementation = CreateReservationDTO::class))]
        )
        @Valid @Body request: CreateReservationDTO
    ): HttpResponse<ReservationDetailsDTO> {
        val reservation = reservationService.createReservation(request.customerId, request.screeningId, request.seats)

        return HttpResponse.created(ReservationDetailsDTO.from(reservation))
    }

    /**
     * Retrieves details of a specific reservation.
     *
     * @param id the reservation ID (must be positive)
     * @return reservation details including all reserved seats
     * @throws NoSuchElementException if reservation not found
     */
    @Operation(
        summary = "Get reservation details",
        description = "Retrieves complete information about a specific reservation including all reserved seats"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Reservation details",
            content = [Content(schema = Schema(implementation = ReservationDetailsDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Reservation not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Get("/{id}")
    suspend fun getReservation(
        @Parameter(description = "Reservation ID", required = true, example = "1")
        @Positive(message = "Reservation ID must be positive")
        @PathVariable id: Long
    ) = reservationService.getReservationDetails(id)

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
    @Operation(
        summary = "Confirm payment",
        description = """
            Confirms payment for a reservation. Must be called within the timeout window
            (default 15 minutes from creation). Changes reservation state from RESERVED to PAID.
        """
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Payment confirmed successfully",
            content = [Content(schema = Schema(implementation = ReservationDetailsDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Reservation not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "409",
            description = "Reservation expired or already paid",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Post("/{id}/payment")
    suspend fun confirmPayment(
        @Parameter(description = "Reservation ID", required = true, example = "1")
        @Positive(message = "Reservation ID must be positive")
        @PathVariable id: Long,
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
    @Operation(
        summary = "Cancel reservation",
        description = """
            Cancels a reservation and releases the seats. Only RESERVED reservations can be cancelled.
            Paid reservations cannot be cancelled (would require separate refund logic).
        """
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "204",
            description = "Reservation cancelled successfully"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Reservation not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "409",
            description = "Cannot cancel paid reservation",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @Delete("/{id}")
    suspend fun cancelReservation(
        @Parameter(description = "Reservation ID", required = true, example = "1")
        @Positive(message = "Reservation ID must be positive")
        @PathVariable id: Long
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
    @Operation(
        summary = "Get customer reservations",
        description = "Retrieves all reservations for a specific customer (all states: RESERVED, PAID, CANCELED)"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "List of customer reservations",
            content = [Content(schema = Schema(implementation = ReservationDetailsDTO::class))]
        )
    )
    @Get("/customer/{customerId}")
    suspend fun getCustomerReservations(
        @Parameter(description = "Customer ID", required = true, example = "1")
        @Positive(message = "Customer ID must be positive")
        @PathVariable customerId: Long
    ) = reservationService.getReservationsByCustomerId(customerId)
}
