package rk.powermilk.cinema.repository

import io.micronaut.context.annotation.Executable
import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import rk.powermilk.cinema.model.Reservation
import java.time.LocalDateTime

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ReservationRepository : CrudRepository<Reservation, Long> {
    fun findByCustomerId(customerId: @NonNull Long): MutableList<Reservation>

    @Query("SELECT * FROM reservation WHERE state = 'RESERVED' AND created_at < :expirationTime")
    @Executable
    fun findExpiredReservations(expirationTime: @NonNull LocalDateTime): MutableList<Reservation>
}
