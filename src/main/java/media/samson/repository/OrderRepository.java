package media.samson.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.PageableRepository;
import media.samson.entity.Order;

import java.math.BigInteger;


@Repository
public interface OrderRepository extends PageableRepository<Order, BigInteger> {
    default Order create(Order order) {
        return save(order);
    }
}
