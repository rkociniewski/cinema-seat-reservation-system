package rk.cinema.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;

@MappedEntity
public record Seat(
        @Id
        @GeneratedValue
        Long id,

        @NonNull
        String row,

        @NonNull
        Integer number,

        @Relation(Relation.Kind.MANY_TO_ONE)
        @NonNull
        Hall hall
) {
}
