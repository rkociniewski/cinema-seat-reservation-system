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
@Requires(classes = [NoSuchElementException::class, ExceptionHandler::class])
class NoSuchElementExceptionHandler : ExceptionHandler<NoSuchElementException, HttpResponse<ErrorResponse>> {
    private val logger = LoggerFactory.getLogger(NoSuchElementExceptionHandler::class.java)

    override fun handle(request: HttpRequest<*>, exception: NoSuchElementException): HttpResponse<ErrorResponse> {
        logger.warn("Resource not found: {}", exception.message)

        return HttpResponse.notFound(
            ErrorResponse(
                status = HttpStatus.NOT_FOUND.code,
                error = "Not Found",
                message = exception.message ?: "Resource not found",
                path = request.path
            )
        )
    }
}
