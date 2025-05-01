package rk.cinema.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public record Hall(
        @Id
        @GeneratedValue
        Long id,

        @NonNull
        String name
) {
}
