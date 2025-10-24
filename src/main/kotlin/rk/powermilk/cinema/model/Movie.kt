package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank

@Serdeable
@Entity
data class Movie(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,

    val title: @NonNull @NotBlank String,

    val durationInMinutes: @NonNull Int,
)

