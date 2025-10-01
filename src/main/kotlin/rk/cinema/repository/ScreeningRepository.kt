package rk.cinema.repository

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import rk.cinema.model.Screening

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ScreeningRepository : CrudRepository<Screening, Long> {
    fun findByMovieId(movieId: @NonNull Long): MutableList<Screening>
}
