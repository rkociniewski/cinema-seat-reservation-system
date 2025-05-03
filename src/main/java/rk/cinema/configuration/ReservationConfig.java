package rk.cinema.configuration;

import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.Min;

@ConfigurationProperties("csrs.reservation")
public class ReservationConfig {

    @Min(1)
    private int timeoutMinutes;

    public int getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(int timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }
}
