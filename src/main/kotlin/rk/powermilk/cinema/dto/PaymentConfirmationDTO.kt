package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.validation.constraints.Size

@Serdeable
@Introspected
data class PaymentConfirmationDTO(
    @field:Size(max = 50, message = "Payment method cannot exceed 50 characters")
    val paymentMethod: String? = null,

    @field:Size(max = 100, message = "Transaction ID cannot exceed 100 characters")
    val transactionId: String? = null
)
