package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import jakarta.persistence.Column

@MappedEntity
data class Seat(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,
    val row: @NonNull String,
    val number: @NonNull Int,
    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    @field:Column("hall_id")
    val hall: @NonNull Hall,
)
