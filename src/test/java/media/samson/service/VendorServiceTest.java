package media.samson.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import media.samson.dto.CreateVendor;
import media.samson.dto.UpdateVendor;
import media.samson.entity.Vendor;
import media.samson.repository.VendorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@MicronautTest
public class VendorServiceTest {
    @Inject
    VendorService vendorService;

    @Inject
    VendorRepository vendorRepository;

    @AfterEach
    public void tearDown() {
        vendorRepository.deleteAll();
    }

    @Test
    public void testCreateVendor() {
        var vendor = vendorService.createVendor(
                new CreateVendor("Acme")
        );

        assertNotEquals(null, vendor.getVendorId());
        assertEquals("Acme", vendor.getName());
    }

    @Test
    public void testReadVendor() {
        var vendor = vendorRepository.create(
                new Vendor("Acme")
        );

        var foundVendor = vendorService.readVendor(vendor.getVendorId());
        assertNotNull(foundVendor);
        assertFalse(foundVendor.isEmpty());
        assertEquals(vendor.getVendorId(), foundVendor.get().getVendorId());
        assertEquals(vendor.getName(), foundVendor.get().getName());
    }

    @Test
    public void testUpdateVendor() {
        var vendor = vendorService.createVendor(
                new CreateVendor("Acme")
        );

        vendorService.updateVendor(
                new UpdateVendor(
                        vendor.getVendorId(),
                        vendor.getName() + " Inc."
                )
        );

        var updatedVendor = vendorRepository.findById(vendor.getVendorId());
        assertFalse(updatedVendor.isEmpty());
        assertEquals(vendor.getVendorId(), updatedVendor.get().getVendorId());
        assertEquals("Acme Inc.", updatedVendor.get().getName());
    }

    @Test
    public void testDeleteVendor() {
        var vendor = vendorService.createVendor(
                new CreateVendor("Acme")
        );

        vendorService.deleteVendor(vendor.getVendorId());

        var missingVendor = vendorRepository.findById(vendor.getVendorId());
        assertTrue(missingVendor.isEmpty());
    }
}
