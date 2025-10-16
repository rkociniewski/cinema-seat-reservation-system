package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(
    description = """
        Optional payment details when confirming a reservation.

        This DTO captures payment metadata for audit and tracking purposes.
        In a production system, this would integrate with a payment gateway
        and include transaction details.

        **Current Implementation:**
        These fields are optional and stored for reference only.
        No actual payment processing is performed.
    """
)
@Serdeable
@Introspected
data class PaymentConfirmationDTO(
    @field:Schema(
        description = """
            Payment method used (e.g., 'credit_card', 'debit_card', 'cash', 'mobile_payment').
            Free-form text field for flexibility.
        """,
        example = "credit_card",
        maxLength = 50,
        nullable = true
    )
    @field:Size(max = 50, message = "Payment method cannot exceed 50 characters")
    val paymentMethod: String? = null,

    @field:Schema(
        description = """
            Transaction ID from payment gateway or internal payment system.
            Used for reconciliation and refund processing.
        """,
        example = "TXN-20251016-ABC123",
        maxLength = 100,
        nullable = true
    )
    @field:Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    val transactionId: String? = null
)
