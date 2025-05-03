package rk.cinema.repository;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import rk.cinema.model.Customer;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    Optional<Customer> findByEmail(@NonNull String email);
}