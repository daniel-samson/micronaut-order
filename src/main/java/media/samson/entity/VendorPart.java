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

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "vendorId", referencedColumnName = "vendorId")
    private Vendor vendor;

    public VendorPart() {
        this.vendorPartId = null;
        this.partName = null;
        this.partDescription = null;
    }

    public VendorPart(BigInteger vendorPartId, String partName, String partDescription, BigDecimal partPrice, Vendor vendor) {
        // import
        this.vendorPartId = vendorPartId;
        this.partName = partName;
        this.partDescription = partDescription;
        this.partPrice = partPrice;
        this.vendor = vendor;
    }

    public VendorPart(String partName, String partDescription, BigDecimal partPrice, Vendor vendor) {
        // create
        this.vendorPartId = null;
        this.partName = partName;
        this.partDescription = partDescription;
        this.partPrice = partPrice;
        this.vendor = vendor;
    }
}
