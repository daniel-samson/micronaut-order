package media.samson.dto;

import media.samson.entity.Order;

import java.math.BigInteger;

public record UpdateOrder(
        BigInteger orderId,
        Order.Status status
) {
}
