package rk.cinema.repository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import rk.cinema.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findByCustomerId(@NonNull Long customerId);

    @Query("""
                SELECT * FROM reservation
                WHERE state = 'RESERVED' AND created_at < :expirationTime
            """)
    List<Reservation> findExpiredReservations(@NonNull LocalDateTime expirationTime);
}