package media.samson.service;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import media.samson.dto.CreateVendorPart;
import media.samson.dto.UpdateVendorPart;
import media.samson.entity.Vendor;
import media.samson.entity.VendorPart;
import media.samson.repository.VendorPartRepository;
import media.samson.repository.VendorRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Singleton
public class VendorPartService {
    @Inject
    VendorPartRepository vendorPartRepository;

    @Inject
    VendorRepository vendorRepository;

    public List<VendorPart> getAllVendorParts(Pageable pageable) {
        return vendorPartRepository.findAll(pageable).getContent();
    }

    public VendorPart createVendorPart(CreateVendorPart createVendorPart) {
        if (createVendorPart.vendorId() == null) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "vendorId is required");
        }

        Vendor vendor = vendorRepository.findById(createVendorPart.vendorId())
                .orElseThrow(() -> new RuntimeException("Vendor Part not found"));

        return vendorPartRepository.create(
                new VendorPart(
                        createVendorPart.partName(),
                        createVendorPart.partDescription(),
                        createVendorPart.partPrice(),
                        vendor
                )
        );
    }

    public Optional<VendorPart> readVendorPart(BigInteger vendorPartId) {
        return vendorPartRepository.findById(vendorPartId);
    }

    public void updateVendorPart(UpdateVendorPart updateVendorPart) {
        Vendor vendor = vendorRepository.findById(updateVendorPart.vendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendorPartRepository.update(new VendorPart(
                updateVendorPart.vendorPartId(),
                updateVendorPart.partName(),
                updateVendorPart.partDescription(),
                updateVendorPart.partPrice(),
                vendor
        ));
    }

    public void deleteVendorPart(BigInteger vendorPartId) {
        vendorPartRepository.deleteById(vendorPartId);
    }
}
