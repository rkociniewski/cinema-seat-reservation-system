package rk.powermilk.cinema.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import rk.powermilk.cinema.dto.MovieDTO
import rk.powermilk.cinema.service.MovieService

@Controller("/api/movies")
class MovieController(private val movieService: MovieService) {

    @Get
    suspend fun getAllMovies(): List<MovieDTO> {
        return movieService.getAllMovies()
    }

    @Get("/{id}")
    suspend fun getMovie(@PathVariable id: Long): MovieDTO {
        return movieService.getMovie(id)
    }
}
