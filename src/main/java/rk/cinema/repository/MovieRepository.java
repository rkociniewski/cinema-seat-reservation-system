package rk.cinema.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import rk.cinema.model.Movie;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MovieRepository extends CrudRepository<Movie, Long> {
}