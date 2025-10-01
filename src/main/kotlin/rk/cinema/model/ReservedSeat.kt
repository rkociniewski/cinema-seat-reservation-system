package rk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import rk.cinema.enums.TicketType

@MappedEntity("reserved_seat")
data class ReservedSeat(
    @GeneratedValue @param:Id val id: Long,
    @Relation(Relation.Kind.MANY_TO_ONE) val seat: @NonNull Seat,
    @Relation(Relation.Kind.MANY_TO_ONE) val reservation: @NonNull Reservation,
    val ticketType: @NonNull TicketType,
)
