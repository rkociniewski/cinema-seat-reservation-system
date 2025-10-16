package rk.powermilk.cinema.metrics

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
        Comprehensive business-level statistics about cinema operations.

        This model aggregates key performance indicators (KPIs) across the entire
        cinema system. Useful for dashboards, reports, and business analytics.

        **Calculation Notes:**
        - Metrics are calculated in real-time from the database
        - occupancyRate is based on all seats across all halls
        - Statistics include reservations in all states (RESERVED, PAID, CANCELED)
    """
)
@Serdeable
data class CinemaStats(
    @param:Schema(
        description = """
            Total number of all reservations ever created in the system.
            Includes reservations in all states: RESERVED, PAID, and CANCELED.
        """,
        example = "150",
        required = true
    )
    val totalReservations: Long,

    @param:Schema(
        description = """
            Number of reservations currently in RESERVED state.

            These are pending reservations that:
            - Have not been paid yet
            - Have not expired
            - Have not been manually canceled

            This metric shows current booking activity.
        """,
        example = "23",
        required = true
    )
    val activeReservations: Long,

    @param:Schema(
        description = """
            Total number of successfully completed (PAID) reservations.

            This represents:
            - Confirmed bookings
            - Revenue-generating transactions
            - Successfully issued tickets
        """,
        example = "98",
        required = true
    )
    val paidReservations: Long,

    @param:Schema(
        description = """
            Total number of cancelled reservations.

            Includes reservations canceled by:
            - User action (manual cancellation)
            - System timeout (unpaid after expiration)
            - Other automated cleanup processes
        """,
        example = "29",
        required = true
    )
    val cancelledReservations: Long,

    @param:Schema(
        description = """
            Total number of movie screenings scheduled in the system.
            Includes past, present, and future screenings.
        """,
        example = "45",
        required = true
    )
    val totalScreenings: Long,

    @param:Schema(
        description = """
            Total seating capacity across all cinema halls.
            This is the maximum number of seats that can be reserved per screening.
        """,
        example = "250",
        required = true
    )
    val totalSeats: Long,

    @param:Schema(
        description = """
            Current number of reserved seats across all screenings.

            Includes seats in both RESERVED and PAID states.
            Does not include seats from CANCELED reservations.
        """,
        example = "87",
        required = true
    )
    val totalReservedSeats: Long,

    @param:Schema(
        description = """
            Percentage of total seats that are currently reserved.

            **Calculation:** (totalReservedSeats / totalSeats) * 100

            **Interpretation:**
            - 0-30%: Low occupancy
            - 30-60%: Moderate occupancy
            - 60-85%: High occupancy
            - 85-100%: Near full capacity

            This metric helps with:
            - Capacity planning
            - Pricing strategies
            - Screening schedule optimization
        """,
        example = "34.8",
        required = true,
        minimum = "0.0",
        maximum = "100.0"
    )
    val occupancyRate: Double
)
