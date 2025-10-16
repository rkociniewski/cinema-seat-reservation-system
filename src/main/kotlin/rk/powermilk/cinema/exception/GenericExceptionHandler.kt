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
@Requires(classes = [Exception::class, ExceptionHandler::class])
class GenericExceptionHandler : ExceptionHandler<Exception, HttpResponse<ErrorResponse>> {
    private val logger = LoggerFactory.getLogger(GenericExceptionHandler::class.java)

    override fun handle(request: HttpRequest<*>, exception: Exception): HttpResponse<ErrorResponse> {
        logger.error("Unexpected error occurred", exception)

        return HttpResponse.serverError(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.code,
                error = "Internal Server Error",
                message = "An unexpected error occurred",
                path = request.path
            )
        )
    }
}
