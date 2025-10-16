package rk.powermilk.cinema.model.error

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
        Validation error response returned when request data fails validation.

        This response is returned when the request body or parameters don't meet
        the defined validation constraints (e.g., missing required fields,
        values out of range, invalid format).

        **HTTP Status Code:** Always 400 Bad Request

        **Example Scenarios:**
        - Missing required field (customerId is null)
        - Value out of range (reserving 25 seats, max is 20)
        - Invalid format (negative seat ID)
        - Empty collections (no seats selected)
    """
)
@Serdeable
data class ValidationErrorResponse(
    @param:Schema(
        description = "HTTP status code (always 400 for validation errors)",
        example = "400",
        required = true
    )
    val status: Int,

    @param:Schema(
        description = "Error category",
        example = "Validation Failed",
        required = true
    )
    val error: String,

    @param:Schema(
        description = "General validation failure message",
        example = "Request validation failed",
        required = true
    )
    val message: String,

    @param:Schema(
        description = "API endpoint path where the validation failed",
        example = "/api/reservations",
        nullable = true
    )
    val path: String? = null,

    @param:Schema(
        description = "List of specific field validation errors",
        required = true
    )
    val violations: List<FieldError>
)
