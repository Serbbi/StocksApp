package nl.rug.aoop.util.order;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class OrderTest {
    private Order getOrder() {
        return new Order(OrderType.BUY, "SOMEID", "ABC", 10.0, 10);
    }

    @Test
    void testToJson() {
        Gson gson = new Gson();
        Order order = getOrder();
        log.info(order.toJson());
        assertEquals(order.toJson(), gson.toJson(order));
    }

    @Test
    void testFromJson() {
        Gson gson = new Gson();
        Order order = getOrder();
        String json = gson.toJson(order);

        Order newOrder = Order.fromJson(json);
        assertEquals(order.getStock(), newOrder.getStock());

    }
}