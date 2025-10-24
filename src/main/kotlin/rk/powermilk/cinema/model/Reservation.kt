package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Relation
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import rk.powermilk.cinema.enums.ReservationState
import java.time.LocalDateTime

@Serdeable
@Entity
data class Reservation(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,

    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    @field:Column("screening_id")
    val screening: @NonNull Screening,

    @field:Column("created_at")
    val createdAt: @NonNull LocalDateTime,

    @Enumerated(EnumType.ORDINAL)
    val state: @NonNull ReservationState,

    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    @field:Column("customer_id")
    val customer: @NonNull Customer,
)

