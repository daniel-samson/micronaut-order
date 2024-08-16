package media.samson.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Serdeable
@Entity
@Table(name = "order_line_items")
public class OrderLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private BigInteger orderLineItemId;

    @Getter
    @Setter
    private Integer quantity;

    public OrderLineItem() {
        this.orderLineItemId = null;
        this.quantity = 0;
    }

    public OrderLineItem(BigInteger orderLineItemId, Integer quantity) {
        this.orderLineItemId = orderLineItemId;
        this.quantity = quantity;
    }

    public OrderLineItem(Integer quantity) {
        this.orderLineItemId = null;
        this.quantity = quantity;
    }
}
