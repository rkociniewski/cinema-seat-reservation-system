package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import jakarta.persistence.Column
import java.time.LocalDateTime

@MappedEntity
data class Screening(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,

    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    @field:Column("movie_id")
    val movie: Movie?,

    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    @field:Column("hall_id")
    val hall: Hall?,

    @field:Column("start_time")
    val startTime: @NonNull LocalDateTime,
)

