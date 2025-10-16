package rk.powermilk.cinema.exception

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import rk.powermilk.cinema.model.ErrorResponse

@Produces
@Singleton
@Requires(classes = [IllegalStateException::class, ExceptionHandler::class])
class IllegalStateExceptionHandler : ExceptionHandler<IllegalStateException, HttpResponse<ErrorResponse>> {
    private val logger = LoggerFactory.getLogger(IllegalStateExceptionHandler::class.java)

    override fun handle(request: HttpRequest<*>, exception: IllegalStateException): HttpResponse<ErrorResponse> {
        logger.warn("Invalid state: {}", exception.message)

        return HttpResponse.status<ErrorResponse>(HttpStatus.CONFLICT).body(
            ErrorResponse(
                status = HttpStatus.CONFLICT.code,
                error = "Conflict",
                message = exception.message ?: "Operation cannot be performed",
                path = request.path
            )
        )
    }
}
