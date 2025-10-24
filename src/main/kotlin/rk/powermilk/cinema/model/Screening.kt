package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Relation
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDateTime

@Serdeable
@Entity
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

