package media.samson.entity;

import lombok.Getter;
import lombok.Setter;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Serdeable
@Entity
@Table(name = "orders")
public class Order {
        // Properties
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Getter
        @Setter
        private BigInteger orderId;

        @Getter
        @Setter
        @Enumerated(EnumType.STRING)
        private Status status;

        @Getter
        @Setter
        @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
        @JoinColumn(name = "orderId", referencedColumnName = "orderId", insertable = true, updatable = false)
        private List<OrderLineItem> lineItems;

        // Constructors

        public Order() {
                this.orderId = null;
                this.status = Status.CREATED;
        }

        public Order(BigInteger orderId, Status status, List<OrderLineItem> orderLineItems) {
                this.orderId = orderId;
                this.status = status;
                this.lineItems = orderLineItems;
        }

        public Order(BigInteger orderId, Status status) {
                // create
                this.orderId = orderId;
                this.status = status;
                this.lineItems = new ArrayList<OrderLineItem>();
        }

        public enum Status {
                CANCELLED,
                CREATED,
                PENDING,
                SHIPPED,
                DELIVERED
        }
}