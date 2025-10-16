package rk.powermilk.cinema.exception

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import rk.powermilk.cinema.model.error.FieldError
import rk.powermilk.cinema.model.error.ValidationErrorResponse

@Produces
@Singleton
@Requires(classes = [ConstraintViolationException::class, ExceptionHandler::class])
class ConstraintViolationExceptionHandler :
    ExceptionHandler<ConstraintViolationException, HttpResponse<ValidationErrorResponse>> {
    private val logger = LoggerFactory.getLogger(ConstraintViolationExceptionHandler::class.java)

    override fun handle(
        request: HttpRequest<*>,
        exception: ConstraintViolationException
    ): HttpResponse<ValidationErrorResponse> {
        logger.warn("Validation failed: {}", exception.message)

        val violations = exception.constraintViolations.map {
            FieldError(it.propertyPath.toString(), it.message, it.invalidValue?.toString())
        }

        return HttpResponse.badRequest(
            ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.code,
                "Validation Failed",
                "Request validation failed",
                request.path,
                violations
            )
        )
    }
}
