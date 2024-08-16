package media.samson.entity;

import lombok.Getter;
import lombok.Setter;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

import java.math.BigInteger;
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
        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private List<OrderLineItem> lineItems;

        // Constructors

        public Order() {
                this.orderId = null;
                this.status = Status.PENDING;
        }

        public Order(BigInteger orderId, Status status, List<OrderLineItem> orderLineItems) {
                this.orderId = orderId;
                this.status = status;
                this.lineItems = orderLineItems;
        }

        public enum Status {
                CANCELLED,
                PENDING,
                SHIPPED,
                DELIVERED
        }
}