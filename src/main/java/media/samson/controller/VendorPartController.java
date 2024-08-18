package media.samson.controller;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import media.samson.dto.CreateVendorPart;
import media.samson.dto.UpdateVendorPart;
import media.samson.entity.VendorPart;
import media.samson.service.VendorPartService;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Controller("/vendor-part")
public class VendorPartController {
    @Inject
    private final VendorPartService vendorPartService;

    public VendorPartController(VendorPartService vendorPartService) {
        this.vendorPartService = vendorPartService;
    }

    @Get
    public List<VendorPart> index(@Valid Pageable pageable) {
        return vendorPartService.getAllVendorParts(pageable);
    }

    @Post
    public HttpResponse<VendorPart> create(@Body CreateVendorPart vendorPart) {
        return HttpResponse.created(vendorPartService.createVendorPart(vendorPart));
    }

    @Get("/{vendorPartId}")
    public Optional<VendorPart> read(BigInteger vendorPartId) {
        return vendorPartService.readVendorPart(vendorPartId);
    }

    @Put
    @Status(HttpStatus.NO_CONTENT)
    public void update(@Body UpdateVendorPart vendorPart) {
        vendorPartService.updateVendorPart(vendorPart);
    }

    @Delete("/{vendorPartId}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(BigInteger vendorPartId) {
        vendorPartService.deleteVendorPart(vendorPartId);
    }
}
