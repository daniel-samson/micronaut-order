package media.samson.service;

import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import media.samson.dto.CreateVendor;
import media.samson.dto.UpdateVendor;
import media.samson.entity.Vendor;
import media.samson.repository.VendorRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Singleton
public class VendorService {
    @Inject
    private VendorRepository vendorRepository;

    public List<Vendor> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable).getContent();
    }

    public Vendor createVendor(CreateVendor vendor) {
        return vendorRepository.create(
                new Vendor(
                        vendor.name()
                )
        );
    }

    public Optional<Vendor> readVendor(BigInteger vendorId) {
        return vendorRepository.findById(vendorId);
    }

    public void updateVendor(UpdateVendor vendor) {
        vendorRepository.update(
                new Vendor(
                        vendor.vendorId(),
                        vendor.name()
                )
        );
    }

    public void deleteVendor(BigInteger vendorId) {
        vendorRepository.deleteById(vendorId);
    }
}
