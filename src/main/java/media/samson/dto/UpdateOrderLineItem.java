package media.samson.dto;


import io.micronaut.serde.annotation.Serdeable;

import java.math.BigInteger;

@Serdeable
public record UpdateOrderLineItem(
        BigInteger orderLineItemId,
        Integer quantity
) {
}
