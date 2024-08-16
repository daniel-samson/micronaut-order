package media.samson.entity;

import lombok.Getter;
import lombok.Setter;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;

import java.math.BigInteger;



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

        // Constructors

        public Order() {
                this.orderId = null;
                this.status = Status.PENDING;
        }

        public Order(BigInteger orderId, Status status) {
                this.orderId = orderId;
                this.status = status;
        }

        public enum Status {
                CANCELLED,
                PENDING,
                SHIPPED,
                DELIVERED
        }
}