package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import jakarta.validation.constraints.NotBlank

@MappedEntity
data class Movie(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,
    val title: @NonNull @NotBlank String,
    val durationInMinutes: @NonNull Int,
)

