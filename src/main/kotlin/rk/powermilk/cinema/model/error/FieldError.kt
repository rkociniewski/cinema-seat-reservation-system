package rk.powermilk.cinema.model.error

import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    description = """
        Detailed validation error information for a single field.

        Provides specific information about what validation rule was violated
        and what value caused the violation.
    """
)
@Serdeable
data class FieldError(
    @param:Schema(
        description = "Name of the field that failed validation (JSON path notation)",
        example = "seats",
        required = true
    )
    val field: String,

    @param:Schema(
        description = "Validation error message explaining the constraint",
        example = "Can reserve between 1 and 20 seats",
        required = true
    )
    val message: String,

    @param:Schema(
        description = "The value that was rejected (converted to string for display)",
        example = "{}",
        nullable = true
    )
    val rejectedValue: String?
)
