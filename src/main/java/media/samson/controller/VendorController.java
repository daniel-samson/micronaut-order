package media.samson.controller;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import media.samson.dto.CreateVendor;
import media.samson.dto.UpdateVendor;
import media.samson.entity.Vendor;
import media.samson.service.VendorService;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Controller("/vendor")
public class VendorController {
    @Inject
    private final VendorService vendorService;

  public VendorController(VendorService vendorService) {
      this.vendorService = vendorService;
  }

    @Get
    public List<Vendor> index(@Valid Pageable pageable) {
        return vendorService.getAllVendors(pageable);
    }

    @Post
    public HttpResponse<Vendor> create(@Valid @Body CreateVendor vendor) {
        return HttpResponse.created(vendorService.createVendor(vendor));
    }

    @Get("/{vendorId}")
    public Optional<Vendor> read(BigInteger vendorId) {
        return vendorService.readVendor(vendorId);
    }

    @Put
    @Status(HttpStatus.NO_CONTENT)
    public void update(@Body UpdateVendor vendor) {
        vendorService.updateVendor(vendor);
    }

    @Delete("/{vendorId}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(BigInteger vendorId) {
        vendorService.deleteVendor(vendorId);
    }
}
