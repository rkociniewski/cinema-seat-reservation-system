package rk.cinema.repository

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import rk.cinema.model.Seat

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SeatRepository : CrudRepository<Seat, Long> {
    fun findByHallId(hallId: @NonNull Long): @NonNull MutableList<Seat>
}
