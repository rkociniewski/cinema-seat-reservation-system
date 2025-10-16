package rk.powermilk.cinema.model

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null
)
