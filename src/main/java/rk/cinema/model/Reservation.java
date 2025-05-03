package rk.cinema.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import rk.cinema.enums.ReservationState;

import java.time.LocalDateTime;

@MappedEntity
public record Reservation(
        @Id
        @GeneratedValue
        Long id,

        @Relation(Relation.Kind.MANY_TO_ONE)
        @NonNull
        Screening screening,

        @NonNull
        LocalDateTime createdAt,

        @NonNull
        ReservationState state,

        @Relation(Relation.Kind.MANY_TO_ONE)
        @NonNull
        Customer customer
) {
}
