package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import rk.powermilk.cinema.enums.ReservationState
import java.time.LocalDateTime

@MappedEntity
data class Reservation(
    @GeneratedValue @param:Id val id: Long,
    @Relation(Relation.Kind.MANY_TO_ONE) val screening: @NonNull Screening,
    val createdAt: @NonNull LocalDateTime,
    val state: @NonNull ReservationState,
    @Relation(Relation.Kind.MANY_TO_ONE) val customer: @NonNull Customer,
)
