package rk.cinema.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;

@MappedEntity
public record Movie(
        @Id
        @GeneratedValue
        Long id,

        @NonNull
        @NotBlank
        String title,

        @NonNull
        Integer durationInMinutes // czas trwania
) {
}

