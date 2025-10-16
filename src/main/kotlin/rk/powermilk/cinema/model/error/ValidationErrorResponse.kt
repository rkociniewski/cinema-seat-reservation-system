package rk.powermilk.cinema.model.error

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ValidationErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null,
    val violations: List<FieldError>
)
