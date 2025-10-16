package rk.powermilk.cinema.dto

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable
@Introspected
data class PaymentConfirmationDTO(
    val paymentMethod: String? = null,
    val transactionId: String? = null
)
