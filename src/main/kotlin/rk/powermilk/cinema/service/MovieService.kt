package rk.powermilk.cinema.service

import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rk.powermilk.cinema.dto.MovieDTO
import rk.powermilk.cinema.repository.MovieRepository

/**
 * Service for managing movie catalog operations.
 *
 * Provides read-only access to the movie database, allowing users to browse
 * available films and their details. All operations are non-blocking using
 * Kotlin coroutines.
 */
@Singleton
class MovieService(private val movieRepository: MovieRepository) {

    /**
     * Retrieves all movies available in the cinema catalog.
     *
     * Returns a complete list of movies with their basic information
     * (title, duration). This is typically the first step in the booking
     * flow where users browse what's currently showing.
     *
     * @return list of all movies in the system
     */
    @ReadOnly
    suspend fun getAllMovies(): List<MovieDTO> = withContext(Dispatchers.IO) {
        movieRepository.findAll().map { MovieDTO.from(it) }
    }

    /**
     * Retrieves detailed information about a specific movie.
     *
     * Returns complete movie data including title and duration in minutes.
     * Users typically view this when they want more information about a
     * specific film before checking screenings.
     *
     * @param id the unique identifier of the movie
     * @return movie details as [MovieDTO]
     * @throws NoSuchElementException if movie with given ID does not exist
     */
    @ReadOnly
    suspend fun getMovie(id: Long): MovieDTO = withContext(Dispatchers.IO) {
        val movie = movieRepository.findById(id)
            .orElseThrow { NoSuchElementException("Movie not found: $id") }

        MovieDTO.from(movie)
    }
}
