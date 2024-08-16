package media.samson.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.operations.JpaRepositoryOperations;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import media.samson.entity.OrderLineItem;

import java.math.BigInteger;

@Repository
public abstract class OrderLineItemRepository implements CrudRepository<OrderLineItem, BigInteger> {
    @Inject
    JpaRepositoryOperations operations;

    @Transactional
    public void deleteByOrderId(BigInteger orderId, BigInteger orderLineItemId) {
        var em = operations.getCurrentEntityManager();
        // remove the link between orders and line items
        em.createNativeQuery(
                        "DELETE FROM orders_order_line_items " +
                                "WHERE order_order_id = :orderId " +
                                "AND line_items_order_line_item_id = :orderLineItemId")
                .setParameter("orderId", orderId)
                .setParameter("orderLineItemId", orderLineItemId)
                .executeUpdate();
        // remove the order line item
        em.createNativeQuery(
        "DELETE FROM order_line_items " +
                "WHERE order_line_item_id = :orderLineItemId")
                .setParameter("orderLineItemId", orderLineItemId)
                .executeUpdate();
    }
}
