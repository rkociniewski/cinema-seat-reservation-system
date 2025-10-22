package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import jakarta.persistence.Column
import rk.powermilk.cinema.enums.ReservationState
import java.time.LocalDateTime

@MappedEntity
data class Reservation(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,

    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    @field:Column("screening_id")
    val screening: @NonNull Screening,

    @field:Column("created_at")
    val createdAt: @NonNull LocalDateTime,

    val state: @NonNull ReservationState,

    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    @field:Column("customer_id")
    val customer: @NonNull Customer,
)

