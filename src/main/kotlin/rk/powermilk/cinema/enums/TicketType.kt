package rk.powermilk.cinema.enums

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
        Type of ticket with associated pricing tier.

        Different ticket types allow for flexible pricing strategies
        (discounts for children, seniors, etc.). Each seat in a reservation
        must have an assigned ticket type.

        **Pricing Strategy:**
        In a production system, each type would have a different price point.
        The current implementation tracks ticket types but doesn't enforce pricing.
    """,
    example = "STANDARD"
)
enum class TicketType {
    @Schema(
        description = """
            Discounted ticket for children.

            **Typical Use:**
            - Age: Under 12 years
            - Discount: ~30-50% off standard price
            - Verification: May require ID check at cinema
        """
    )
    CHILD_DISCOUNT,

    @Schema(
        description = """
            Discounted ticket for senior citizens.

            **Typical Use:**
            - Age: 60+ or 65+ years (varies by region)
            - Discount: ~20-40% off standard price
            - Verification: May require ID check at cinema
        """
    )
    SENIOR_DISCOUNT,

    @Schema(
        description = """
            Regular price ticket for adults.

            **Typical Use:**
            - Default ticket type
            - Full price
            - No age restrictions
        """
    )
    STANDARD
}
