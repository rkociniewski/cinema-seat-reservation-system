package rk.powermilk.cinema.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
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
class DatabaseTestBase : TestPropertyProvider {

    @Container
    private val postgresContainer = PostgreSQLContainer("postgres:16-alpine").apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
        withReuse(true)
    }

    fun dbStart() {
        postgresContainer.start()
    }

    override fun getProperties(): Map<String, String> {
        if (!postgresContainer.isRunning) {
            postgresContainer.start()
        }
        return mapOf(
            "datasources.default.url" to postgresContainer.jdbcUrl,
            "datasources.default.username" to postgresContainer.username,
            "datasources.default.password" to postgresContainer.password
        )
    }

    fun dbStop() = postgresContainer.stop()
}
