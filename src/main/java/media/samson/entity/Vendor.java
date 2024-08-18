package media.samson.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Serdeable
@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private BigInteger vendorId;

    @Getter
    @Setter
    private String name;

    public Vendor() {
        vendorId = null;
        name = null;
    }

    public Vendor(BigInteger vendorId, String name) {
        // import
        this.vendorId = vendorId;
        this.name = name;
    }

    public Vendor(String name) {
        this.vendorId = null;
        this.name = name;
    }


}
