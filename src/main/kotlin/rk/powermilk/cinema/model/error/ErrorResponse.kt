package rk.powermilk.cinema.model.error

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
        Standard error response returned by the API for non-validation errors.

        This structure is used for business logic errors, not found errors,
        and other exceptional cases. For validation errors, see ValidationErrorResponse.

        **Common HTTP Status Codes:**
        - 400 Bad Request - Invalid business logic (e.g., seat already taken)
        - 404 Not Found - Resource doesn't exist
        - 409 Conflict - Invalid state transition (e.g., canceling paid reservation)
        - 500 Internal Server Error - Unexpected server error
    """
)
@Serdeable
data class ErrorResponse(
    @param:Schema(
        description = "HTTP status code",
        example = "404",
        required = true
    )
    val status: Int,

    @param:Schema(
        description = "Short error name/category",
        example = "Not Found",
        required = true
    )
    val error: String,

    @param:Schema(
        description = "Detailed human-readable error message explaining what went wrong",
        example = "Reservation not found: 123",
        required = true
    )
    val message: String,

    @param:Schema(
        description = "API endpoint path where the error occurred",
        example = "/api/reservations/123",
        nullable = true
    )
    val path: String? = null
)


