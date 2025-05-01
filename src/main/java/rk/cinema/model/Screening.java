package rk.cinema.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;

import java.time.LocalDateTime;

@MappedEntity
public record Screening(
        @Id
        @GeneratedValue
        Long id,

        @Relation(Relation.Kind.MANY_TO_ONE)
        @NonNull
        Movie movie,

        @Relation(Relation.Kind.MANY_TO_ONE)
        @NonNull
        Hall hall,

        @NonNull
        LocalDateTime startTime
) {
}
