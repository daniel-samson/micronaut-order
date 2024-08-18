package media.samson.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.operations.JpaRepositoryOperations;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import media.samson.entity.OrderLineItem;

import java.math.BigInteger;
import java.util.List;

@Repository
public abstract class OrderLineItemRepository implements CrudRepository<OrderLineItem, BigInteger> {
    @Inject
    JpaRepositoryOperations operations;

    public OrderLineItem create(OrderLineItem orderLineItem) {
        return this.save(orderLineItem);
    }

    public List<OrderLineItem> findAllByOrderId(BigInteger orderId) {
        var em = operations.getCurrentEntityManager();
        return em.createNativeQuery(
                "select * from order_line_items where order_id = :orderId",
                        OrderLineItem.class
                )
                .setParameter("orderId", orderId)
                .getResultList();
    }

    @Transactional
    public void deleteByOrderId(BigInteger orderId, BigInteger orderLineItemId) {
        var em = operations.getCurrentEntityManager();
        em.createNativeQuery(
                        "DELETE FROM order_line_items " +
                                "WHERE order_id = :orderId " +
                                "AND order_line_item_id = :orderLineItemId")
                .setParameter("orderId", orderId)
                .setParameter("orderLineItemId", orderLineItemId)
                .executeUpdate();
    }
}
