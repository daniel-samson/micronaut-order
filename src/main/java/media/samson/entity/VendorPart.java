package media.samson.entity;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Serdeable
@Entity
@Table(name = "vendor_parts")
public class VendorPart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private BigInteger vendorPartId;

    @Getter
    @Setter
    private String partName;

    @Getter
    @Setter
    private String partDescription;

    @Getter
    @Setter
    private BigDecimal partPrice;

    @ManyToOne
    @JoinColumn(name = "vendorId")
    private Vendor vendor;

    public VendorPart() {
        this.vendorPartId = null;
        this.partName = null;
        this.partDescription = null;
    }

    public VendorPart(BigInteger vendorPartId, String partName, String partDescription, BigDecimal partPrice) {
        this.vendorPartId = vendorPartId;
        this.partName = partName;
        this.partDescription = partDescription;
        this.partPrice = partPrice;
    }

    public VendorPart(String partName, String partDescription, BigDecimal partPrice) {
        this.vendorPartId = null;
        this.partName = partName;
        this.partDescription = partDescription;
        this.partPrice = partPrice;
    }
}
