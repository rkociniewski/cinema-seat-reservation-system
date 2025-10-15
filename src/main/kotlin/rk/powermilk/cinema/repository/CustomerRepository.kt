package rk.powermilk.cinema.repository

import io.micronaut.context.annotation.Executable
import io.micronaut.core.annotation.NonNull
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import rk.powermilk.cinema.model.Customer

@JdbcRepository(dialect = Dialect.POSTGRES)
interface CustomerRepository : CrudRepository<Customer, Long> {
    @Executable
    fun findByEmail(email: @NonNull String): Customer
}
