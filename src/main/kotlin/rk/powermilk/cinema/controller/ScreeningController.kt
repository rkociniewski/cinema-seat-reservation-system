package rk.powermilk.cinema.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import rk.powermilk.cinema.dto.ScreeningDetailsDTO
import rk.powermilk.cinema.dto.ScreeningListDTO
import rk.powermilk.cinema.dto.SeatAvailabilityDTO
import rk.powermilk.cinema.service.ScreeningService

@Controller("/api/screenings")
class ScreeningController(private val screeningService: ScreeningService) {

    @Get
    suspend fun getAllScreenings(): List<ScreeningListDTO> {
        return screeningService.getAllScreenings()
    }

    @Get("/{id}")
    suspend fun getScreeningDetails(@PathVariable id: Long): ScreeningDetailsDTO {
        return screeningService.getScreeningDetails(id)
    }

    @Get("/{id}/seats")
    suspend fun getAvailableSeats(@PathVariable id: Long): List<SeatAvailabilityDTO> {
        return screeningService.getAvailableSeatsForScreening(id)
    }

    @Get("/movie/{movieId}")
    suspend fun getScreeningsByMovie(@PathVariable movieId: Long): List<ScreeningListDTO> {
        return screeningService.getScreeningsByMovie(movieId)
    }
}
