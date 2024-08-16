package media.samson.controller;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import media.samson.entity.Order;
import media.samson.repository.OrderRepository;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Controller("/order")
public class OrderController {
    @Inject
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Get
    public List<Order> index(@Valid Pageable pageable) {
        return orderRepository.findAll(pageable).getContent();
    }

    @Post
    public HttpResponse<?> create(@Body() Order order) {
        var created = orderRepository.create(order);
        return HttpResponse.created(created);
    }

    @Get("/{id}")
    public Optional<Order> read(BigInteger id) {
        return orderRepository.findById(id);
    }

    @Put
    @Status(HttpStatus.NO_CONTENT)
    public void update(@Body Order order) {
        orderRepository.update(order);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(BigInteger id) {
        orderRepository.deleteById(id);
    }
}
