package media.samson.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import media.samson.entity.Order;
import media.samson.entity.OrderLineItem;

import java.math.BigInteger;
import java.util.List;

@Repository
public abstract class OrderLineItemRepository implements CrudRepository<OrderLineItem, BigInteger> {
    @Inject
    private EntityManager entityManager;

    public OrderLineItem create(OrderLineItem orderLineItem) {
        return save(orderLineItem);
    }

    public List<OrderLineItem> findByOrder(Order order) {
        String jpql = "SELECT oli FROM OrderLineItem oli WHERE oli.order = :order";
        return entityManager.createQuery(jpql, OrderLineItem.class)
                .setParameter("order", order)
                .getResultList();
    }

    public void deleteByOrderId(Order order, OrderLineItem orderLineItem) {
        String jpql = "DELETE FROM OrderLineItem e WHERE e.order = :order AND e.orderLineItemId = :orderLineItemId";
//        entityManager.createNativeQuery(
//                        "DELETE FROM order_line_items " +
//                                "WHERE order_id = :orderId " +
//                                "AND order_line_item_id = :orderLineItemId")
//                .setParameter("orderId", orderId)
//                .setParameter("orderLineItemId", orderLineItemId)
        entityManager.createQuery(jpql)
                .setParameter("order", order)
                .setParameter("orderLineItemId", orderLineItem.getOrderLineItemId())
                .executeUpdate();
    }
}
