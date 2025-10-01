package rk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import jakarta.validation.constraints.NotBlank

@MappedEntity
data class Customer(
    @GeneratedValue @param:Id val id: Long,
    val email: @NonNull @NotBlank String,
    val name: @NonNull @NotBlank String,
)
