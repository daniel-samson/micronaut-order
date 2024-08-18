package media.samson.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import media.samson.dto.CreateVendorPart;
import media.samson.dto.UpdateVendorPart;
import media.samson.entity.Vendor;
import media.samson.repository.VendorPartRepository;
import media.samson.repository.VendorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


@MicronautTest
public class VendorPartServiceTest {
    @Inject
    VendorPartService vendorPartService;

    @Inject
    VendorPartRepository vendorPartRepository;

    @Inject
    VendorRepository vendorRepository;

    @AfterEach
    public void tearDown() {
        vendorRepository.deleteAll();
        vendorPartRepository.deleteAll();
    }

    @Test
    public void testCreateVendor() {
        var vendor = vendorRepository.save(new Vendor("Acme"));
        var vendorPart = vendorPartService.createVendorPart(
                new CreateVendorPart(
                        "Lemonade",
                        "A Fizzy lemon flavored drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        assertNotEquals(null, vendorPart.getVendorPartId());
        assertEquals("Lemonade", vendorPart.getPartName());
        assertEquals("A Fizzy lemon flavored drink", vendorPart.getPartDescription());
        assertEquals(BigDecimal.ONE, vendorPart.getPartPrice());
        assertEquals(vendor.getVendorId(), vendorPart.getVendor().getVendorId());
    }

    @Test
    public void testReadVendor() {
        var vendor = vendorRepository.save(new Vendor("Acme"));
        var vendorPart = vendorPartService.createVendorPart(
                new CreateVendorPart(
                        "Lemonade",
                        "A Fizzy lemon flavored drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        var foundVendorPart = vendorPartService.readVendorPart(vendorPart.getVendorPartId());
        assertNotNull(foundVendorPart);
        assertFalse(foundVendorPart.isEmpty());
        assertEquals(vendorPart.getVendorPartId(), foundVendorPart.get().getVendorPartId());
        assertEquals(vendorPart.getPartName(), foundVendorPart.get().getPartName());
        assertEquals(vendorPart.getPartDescription(), foundVendorPart.get().getPartDescription());
        assertEquals(vendorPart.getPartPrice(), foundVendorPart.get().getPartPrice());
        assertEquals(vendor.getVendorId(), vendorPart.getVendor().getVendorId());
    }

    @Test
    public void testUpdateVendor() {
        var vendor = vendorRepository.save(new Vendor("Acme"));
        var vendorPart = vendorPartService.createVendorPart(
                new CreateVendorPart(
                        "Lemonade",
                        "A Fizzy lemon flavored drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        vendorPartService.updateVendorPart(
                new UpdateVendorPart(
                        vendorPart.getVendorPartId(),
                        "Cola",
                        "A Fizzy soda drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        var updatedVendorPart = vendorPartRepository.findById(vendorPart.getVendorPartId());
        assertFalse(updatedVendorPart.isEmpty());
        assertEquals(vendorPart.getVendorPartId(), updatedVendorPart.get().getVendorPartId());
        assertEquals("Cola", updatedVendorPart.get().getPartName());
        assertEquals("A Fizzy soda drink", updatedVendorPart.get().getPartDescription());
        assertEquals(BigDecimal.ONE, updatedVendorPart.get().getPartPrice());
        assertEquals(vendor.getVendorId(), updatedVendorPart.get().getVendor().getVendorId());
    }

    @Test
    public void testDeleteVendor() {
        var vendor = vendorRepository.save(new Vendor("Acme"));
        var vendorPart = vendorPartService.createVendorPart(
                new CreateVendorPart(
                        "Lemonade",
                        "A Fizzy lemon flavored drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        vendorPartService.deleteVendorPart(vendorPart.getVendorPartId());

        var missingVendor = vendorPartRepository.findById(vendorPart.getVendorPartId());
        assertTrue(missingVendor.isEmpty());
    }
}
