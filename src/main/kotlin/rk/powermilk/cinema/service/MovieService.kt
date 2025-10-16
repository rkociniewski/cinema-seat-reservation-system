package rk.powermilk.cinema.service

import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rk.powermilk.cinema.dto.MovieDTO
import rk.powermilk.cinema.repository.MovieRepository

@Singleton
class MovieService(private val movieRepository: MovieRepository) {
    suspend fun getAllMovies(): List<MovieDTO> = withContext(Dispatchers.IO) {
        movieRepository.findAll()
            .map { MovieDTO.from(it) }
    }

    suspend fun getMovie(id: Long): MovieDTO = withContext(Dispatchers.IO) {
        val movie = movieRepository.findById(id)
            .orElseThrow { NoSuchElementException("Movie not found: $id") }

        MovieDTO.from(movie)
    }
}
