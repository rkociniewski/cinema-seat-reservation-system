package rk.powermilk.cinema.metrics

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(
    description = """
        Simple health check response for monitoring application status.

        Provides basic operational status information for use with:
        - Load balancers
        - Monitoring systems (Nagios, Prometheus, etc.)
        - Kubernetes health probes
        - Quick operational checks

        **Status Interpretation:**
        - "UP" = Application is healthy and operational
        - "DOWN" = Application has issues (check database field for details)
    """
)
@Serdeable
data class HealthCheck(
    @param:Schema(
        description = """
            Overall application health status.

            **Possible Values:**
            - "UP" - Application is running normally
            - "DOWN" - Application or critical components are failing
        """,
        example = "UP",
        required = true,
        allowableValues = ["UP", "DOWN"]
    )
    val status: String,

    @param:Schema(
        description = """
            Database connection status with details.

            **Possible Values:**
            - "Connected" - Database is accessible and responsive
            - "Error: [message]" - Database connection failed with specific error

            The database is the most critical dependency, so this field provides
            insight into whether the system can persist and retrieve data.
        """,
        example = "Connected",
        required = true
    )
    val database: String,

    @param:Schema(
        description = "Timestamp when the health check was performed (server time)",
        example = "2025-10-16T14:30:00",
        required = true
    )
    val timestamp: LocalDateTime
)
