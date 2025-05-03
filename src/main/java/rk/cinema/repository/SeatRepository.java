package rk.cinema.repository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import rk.cinema.model.Seat;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface SeatRepository extends CrudRepository<Seat, Long> {
    @NonNull
    List<Seat> findByHallId(@NonNull Long hallId);
}