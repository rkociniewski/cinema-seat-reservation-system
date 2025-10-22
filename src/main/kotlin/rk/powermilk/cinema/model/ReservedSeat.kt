package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import jakarta.persistence.Column
import rk.powermilk.cinema.enums.TicketType

@MappedEntity("reserved_seat")
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
    val ticketType: @NonNull TicketType,
)
