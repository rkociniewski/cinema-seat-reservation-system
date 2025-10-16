package rk.powermilk.cinema.model.error

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class FieldError(
    val field: String,
    val message: String,
    val rejectedValue: String?
)
