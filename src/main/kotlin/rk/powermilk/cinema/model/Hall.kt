package rk.powermilk.cinema.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Serdeable
@Entity
data class Hall(
    @field:Id
    @field:GeneratedValue
    val id: Long = 0,

    val name: @NonNull String,
)



