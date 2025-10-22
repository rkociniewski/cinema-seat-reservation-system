package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity
data class Hall(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,
    val name: @NonNull String,
)



