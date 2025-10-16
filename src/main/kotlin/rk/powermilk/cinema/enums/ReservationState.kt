package rk.powermilk.cinema.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
        Current state of a reservation in the booking lifecycle.

        **State Transitions:**
        - FREE → RESERVED (when reservation is created)
        - RESERVED → PAID (when payment is confirmed within timeout)
        - RESERVED → CANCELED (when user cancels or timeout expires)
        - PAID → (final state, cannot be changed)
        - CANCELED → (final state, cannot be changed)
    """,
    example = "RESERVED"
)
enum class ReservationState {
    @Schema(
        description = """
            Initial state
        """
    )
    FREE,

    @Schema(
        description = """
            Seats are temporarily held for the customer.

            **Characteristics:**
            - Seats are blocked for other customers
            - Reservation has an expiration time (default: 15 minutes)
            - Can transition to PAID or CANCELED
            - Automatically canceled after timeout expires

            **Typical Duration:** 15 minutes (configurable)
        """
    )
    RESERVED,

    @Schema(
        description = """
            Reservation was cancelled - either manually by customer or automatically after timeout.

            **Characteristics:**
            - Final state - cannot be changed
            - Seats are released and become available again
            - Reservation record is kept for audit purposes

            **Common Reasons:**
            - User manually cancelled
            - Payment timeout expired
            - System cleanup of old reservations
        """
    )
    CANCELED,

    @Schema(
        description = """
            Payment confirmed - reservation is complete and tickets are issued.

            **Characteristics:**
            - Final state - cannot be cancelled
            - Seats are permanently assigned to this customer
            - Customer receives confirmation (in real system)
            - Cannot be modified or refunded through API

            **Note:** Refunds would require separate business logic not implemented in current version.
        """
    )
    PAID
}
