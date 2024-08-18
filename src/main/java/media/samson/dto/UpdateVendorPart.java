package media.samson.dto;


import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.math.BigInteger;

@Serdeable
public record UpdateVendorPart(
        BigInteger vendorPartId,
        String partName,
        String partDescription,
        BigDecimal partPrice,
        BigInteger vendorId
) {
}
