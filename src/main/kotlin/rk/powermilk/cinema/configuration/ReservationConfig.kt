package rk.powermilk.cinema.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import jakarta.validation.constraints.Min

@ConfigurationProperties("csrs.reservation")
class ReservationConfig {
    @JvmField
    var timeoutMinutes: @Min(1) Int = 0
}
