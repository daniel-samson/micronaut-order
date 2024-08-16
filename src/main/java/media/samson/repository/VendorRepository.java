package media.samson.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import media.samson.entity.Vendor;

import java.math.BigInteger;

@Repository
public interface VendorRepository extends PageableRepository<Vendor, BigInteger> {
    default Vendor create(Vendor vendor) {
        return this.save(vendor);
    }
}
