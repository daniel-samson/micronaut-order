package media.samson.controller;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import media.samson.entity.Vendor;
import media.samson.repository.VendorRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Controller("/vendor")
public class VendorController {
    @Inject
    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Get
    public List<Vendor> index(@Valid Pageable pageable) {
        return vendorRepository.findAll(pageable).getContent();
    }

    @Post
    public HttpResponse<Vendor> create(@Body Vendor vendor) {
        return HttpResponse.created(vendorRepository.create(vendor));
    }

    @Get("/{vendorId}")
    public Optional<Vendor> read(BigInteger vendorId) {
        return vendorRepository.findById(vendorId);
    }

    @Put
    @Status(HttpStatus.NO_CONTENT)
    public void update(@Body Vendor vendor) {
        vendorRepository.update(vendor);
    }

    @Delete("/{vendorId}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(BigInteger vendorId) {
        vendorRepository.deleteById(vendorId);
    }
}
