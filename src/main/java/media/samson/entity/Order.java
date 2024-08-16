package media.samson.entity;

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
        private BigInteger orderId;

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

        // Getters and Setters
        public BigInteger getOrderId() {
                return orderId;
        }

        public void setOrderId(BigInteger orderId) {
                this.orderId = orderId;
        }

        public Status getStatus() {
                return status;
        }

        public void setStatus(Status status) {
                this.status = status;
        }

        public enum Status {
                CANCELLED,
                PENDING,
                SHIPPED,
                DELIVERED
        }
}