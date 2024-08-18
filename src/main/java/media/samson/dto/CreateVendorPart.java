package media.samson.dto;


import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.math.BigInteger;

@Serdeable
public record CreateVendorPart(
        String partName,
        String partDescription,
        BigDecimal partPrice,
        BigInteger vendorId
) {
}
