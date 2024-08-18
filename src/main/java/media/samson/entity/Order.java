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
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Getter
        @Setter
        private BigInteger orderId;

        @Getter
        @Setter
        @Enumerated(EnumType.STRING)
        private Status status;

        // Constructors

        public Order() {
                this.orderId = null;
                this.status = Status.CREATED;
        }

        public Order(BigInteger orderId, Status status) {
                // create
                this.orderId = orderId;
                this.status = status;
        }

        public enum Status {
                CANCELLED,
                CREATED,
                PENDING,
                SHIPPED,
                DELIVERED
        }
}