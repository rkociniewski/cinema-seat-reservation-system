package rk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity
data class Hall(
    @GeneratedValue @param:Id val id: Long,
    val name: @NonNull String,
)
