package rk.cinema.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.validation.constraints.NotBlank;

@MappedEntity
public record Customer(
        @Id
        @GeneratedValue
        Long id,

        @NonNull
        @NotBlank
        String email,

        @NonNull
        @NotBlank
        String name
) {
}
