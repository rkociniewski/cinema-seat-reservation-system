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
import rk.powermilk.cinema.enums.TicketType

@Serdeable
@Entity(name = "reserved_seat")
data class ReservedSeat(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,

    @field:Column("seat_id")
    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    val seat: @NonNull Seat,

    @field:Column("reservation_id")
    @field:Relation(Relation.Kind.MANY_TO_ONE, cascade = [Relation.Cascade.PERSIST])
    val reservation: @NonNull Reservation,

    @Enumerated(EnumType.ORDINAL)
    val ticketType: @NonNull TicketType,
)
