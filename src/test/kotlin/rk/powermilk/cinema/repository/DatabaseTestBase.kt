package rk.powermilk.cinema.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Base class for database integration tests using Testcontainers.
 *
 * Provides a shared PostgreSQL container for all tests to improve performance.
 * The container is started once and reused across all test classes.
 */
@MicronautTest(
    environments = ["test"],
    transactional = false
)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
object DatabaseTestBase {
    @Container
    private val postgresContainer = PostgreSQLContainer("postgres:16-alpine").apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
        withReuse(true)
    }

    fun start() {
        postgresContainer.start()
        System.setProperty("TEST_DB_URL", postgresContainer.jdbcUrl)
        System.setProperty("TEST_DB_USERNAME", postgresContainer.username)
        System.setProperty("TEST_DB_PASSWORD", postgresContainer.password)
    }
}
