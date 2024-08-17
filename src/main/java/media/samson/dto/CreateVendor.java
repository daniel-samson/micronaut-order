package media.samson.dto;


import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

@Serdeable
public record CreateVendor(
        @NotBlank
        String name
) {
}
