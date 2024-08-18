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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private BigInteger orderLineItemId;

    @Getter
    @Setter
    private Integer quantity;


    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private Order order;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendorPartId", insertable = false, updatable = false)
    private VendorPart vendorPart;

    public OrderLineItem() {
        this.orderLineItemId = null;
        this.quantity = 0;
    }

    public OrderLineItem(BigInteger orderLineItemId, Integer quantity, VendorPart vendorPart) {
        // import
        this.orderLineItemId = orderLineItemId;
        this.quantity = quantity;
        this.vendorPart = vendorPart;
    }

    public OrderLineItem(Integer quantity, Order order, VendorPart vendorPart) {
        // create
        this.orderLineItemId = null;
        this.quantity = quantity;
        this.order = order;
        this.vendorPart = vendorPart;
    }

    public OrderLineItem(Integer quantity) {
        // update
        this.orderLineItemId = null;
        this.quantity = quantity;
    }
}
