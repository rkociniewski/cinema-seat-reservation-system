package rk.powermilk.cinema.exception

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import rk.powermilk.cinema.model.error.ErrorResponse

@Produces
@Singleton
@Requires(classes = [IllegalArgumentException::class, ExceptionHandler::class])
class IllegalArgumentExceptionHandler : ExceptionHandler<IllegalArgumentException, HttpResponse<ErrorResponse>> {
    private val logger = LoggerFactory.getLogger(IllegalArgumentExceptionHandler::class.java)

    override fun handle(request: HttpRequest<*>, exception: IllegalArgumentException): HttpResponse<ErrorResponse> {
        logger.warn("Invalid argument: {}", exception.message)

        return HttpResponse.badRequest(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.code,
                error = "Bad Request",
                message = exception.message ?: "Invalid request",
                path = request.path
            )
        )
    }
}
