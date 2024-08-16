package media.samson.controller;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import media.samson.entity.VendorPart;
import media.samson.repository.VendorPartRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Controller("/vendor-part")
public class VendorPartController {
    @Inject
    private final VendorPartRepository vendorPartRepository;

    public VendorPartController(VendorPartRepository vendorPartRepository) {
        this.vendorPartRepository = vendorPartRepository;
    }

    @Get
    public List<VendorPart> index(@Valid Pageable pageable) {
        return vendorPartRepository.findAll(pageable).getContent();
    }

    @Post
    public HttpResponse<VendorPart> create(@Body VendorPart vendorPart) {
        return HttpResponse.created(vendorPartRepository.create(vendorPart));
    }

    @Get("/{vendorId}")
    public Optional<VendorPart> read(BigInteger vendorId) {
        return vendorPartRepository.findById(vendorId);
    }

    @Put
    @Status(HttpStatus.NO_CONTENT)
    public void update(@Body VendorPart vendor) {
        vendorPartRepository.update(vendor);
    }

    @Delete("/{vendorId}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(BigInteger vendorId) {
        vendorPartRepository.deleteById(vendorId);
    }
}
