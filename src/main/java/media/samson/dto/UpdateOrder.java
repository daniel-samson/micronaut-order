package media.samson.dto;

import io.micronaut.serde.annotation.Serdeable;
import media.samson.entity.Order;

import java.math.BigInteger;

@Serdeable
public record UpdateOrder(
        BigInteger orderId,
        Order.Status status
) {
}
