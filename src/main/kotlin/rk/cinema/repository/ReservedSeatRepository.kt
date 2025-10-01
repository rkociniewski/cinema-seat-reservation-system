package rk.cinema.repository

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import rk.cinema.model.ReservedSeat

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ReservedSeatRepository : CrudRepository<ReservedSeat, Long> {
    // Find all reserved seats for specific screening
    @Query(
        "SELECT rs.* FROM reserved_seat rs JOIN reservation r " +
                "ON rs.reservation_id = r.id WHERE r.screening_id = :screeningId"
    )
    fun findByScreeningId(screeningId: @NonNull Long): MutableList<ReservedSeat>

    // Find all reserved seats for specific reservation
    fun findByReservationId(reservationId: @NonNull Long): MutableList<ReservedSeat>

    // Check if seat was taken in specific screening
    @Query(
        "SELECT COUNT(*) > 0 FROM reserved_seat rs JOIN reservation r " +
                "ON rs.reservation_id = r.id WHERE rs.seat_id = :seatId AND r.screening_id = :screeningId"
    )
    fun isSeatTaken(seatId: @NonNull Long, screeningId: @NonNull Long): Boolean
}
