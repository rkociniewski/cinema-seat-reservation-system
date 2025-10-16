package rk.powermilk.cinema.metrics

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class HealthCheck(
    val status: String,
    val database: String,
    val timestamp: java.time.LocalDateTime
)
