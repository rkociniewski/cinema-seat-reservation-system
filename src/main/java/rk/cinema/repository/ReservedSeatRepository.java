package rk.cinema.repository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import rk.cinema.model.ReservedSeat;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReservedSeatRepository extends CrudRepository<ReservedSeat, Long> {
    // Find all reserved seats for specific screening
    @Query("""
                SELECT rs.* FROM reserved_seat rs
                JOIN reservation r ON rs.reservation_id = r.id
                WHERE r.screening_id = :screeningId
            """)
    List<ReservedSeat> findByScreeningId(@NonNull Long screeningId);

    // Find all reserved seats for specific reservation
    List<ReservedSeat> findByReservationId(@NonNull Long reservationId);

    // Check if seat was taken in specific screening
    @Query("""
                SELECT COUNT(*) > 0 FROM reserved_seat rs
                JOIN reservation r ON rs.reservation_id = r.id
                WHERE rs.seat_id = :seatId AND r.screening_id = :screeningId
            """)
    boolean isSeatTaken(@NonNull Long seatId, @NonNull Long screeningId);
}