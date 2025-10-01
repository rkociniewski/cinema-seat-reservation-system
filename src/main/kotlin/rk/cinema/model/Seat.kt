package rk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation

@MappedEntity
data class Seat(
    @GeneratedValue @param:Id val id: Long,
    val row: @NonNull String,
    val number: @NonNull Int,
    @Relation(Relation.Kind.MANY_TO_ONE) val hall: @NonNull Hall,
)
