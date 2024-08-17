package media.samson.dto;


import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

import java.math.BigInteger;

@Serdeable
public record UpdateVendor(
        @NonNull
        BigInteger vendorId,

        @NotBlank
        String name
) {
}
