package rk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import java.time.LocalDateTime

@MappedEntity
data class Screening(
    @GeneratedValue @param:Id val id: Long,
    @Relation(Relation.Kind.MANY_TO_ONE) val movie: Movie?,
    @Relation(Relation.Kind.MANY_TO_ONE) val hall: Hall?,
    val startTime: @NonNull LocalDateTime,
)
