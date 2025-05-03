package rk.cinema.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import rk.cinema.model.Hall;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HallRepository extends CrudRepository<Hall, Long> {
}