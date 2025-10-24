package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank

@Serdeable
@Entity
data class Customer(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,

    val email: @NonNull @NotBlank String,

    val name: @NonNull @NotBlank String,
)
