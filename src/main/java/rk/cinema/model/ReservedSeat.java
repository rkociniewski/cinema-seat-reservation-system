package rk.cinema.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import rk.cinema.enums.TicketType;

@MappedEntity
public record ReservedSeat(
        @Id
        @GeneratedValue
        Long id,

        @Relation(Relation.Kind.MANY_TO_ONE)
        @NonNull
        Seat seat,

        @Relation(Relation.Kind.MANY_TO_ONE)
        @NonNull
        Reservation reservation,

        @NonNull
        TicketType ticketType
) {
}
