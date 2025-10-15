package rk.powermilk.cinema.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import rk.powermilk.cinema.model.Hall

@JdbcRepository(dialect = Dialect.POSTGRES)
interface HallRepository : CrudRepository<Hall, Long>
