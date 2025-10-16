package rk.powermilk.cinema.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import rk.powermilk.cinema.dto.CreateReservationDTO
import rk.powermilk.cinema.dto.PaymentConfirmationDTO
import rk.powermilk.cinema.dto.ReservationDetailsDTO
import rk.powermilk.cinema.service.ReservationService

@Controller("/api/reservations")
class ReservationController(private val reservationService: ReservationService) {
    @Post
    suspend fun createReservation(@Body request: CreateReservationDTO): HttpResponse<ReservationDetailsDTO> {
        val reservation = reservationService.createReservation(
            customerId = request.customerId,
            screeningId = request.screeningId,
            seatIdToTicketType = request.seats
        )

        return HttpResponse.created(ReservationDetailsDTO.from(reservation))
    }

    @Get("/{id}")
    suspend fun getReservation(@PathVariable id: Long): ReservationDetailsDTO {
        return reservationService.getReservationDetails(id)
    }

    @Post("/{id}/payment")
    suspend fun confirmPayment(
        @PathVariable id: Long,
        @Body payment: PaymentConfirmationDTO
    ): HttpResponse<ReservationDetailsDTO> {
        reservationService.confirmPayment(id)
        val updated = reservationService.getReservationDetails(id)
        return HttpResponse.ok(updated)
    }

    @Delete("/{id}")
    suspend fun cancelReservation(@PathVariable id: Long): HttpResponse<Void> {
        reservationService.cancelReservation(id)
        return HttpResponse.noContent()
    }

    @Get("/customer/{customerId}")
    suspend fun getCustomerReservations(@PathVariable customerId: Long): List<ReservationDetailsDTO> {
        return reservationService.getReservationsByCustomerId(customerId)
    }
}
