package media.samson.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import media.samson.entity.VendorPart;

import java.math.BigInteger;

@Repository
public interface VendorPartRepository extends PageableRepository<VendorPart, BigInteger> {
    default VendorPart create(VendorPart vendorPart) {
        return save(vendorPart);
    }
}
