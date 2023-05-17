package nl.rug.aoop.stocksapplication.order;

import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuyOrderQueueTest {
    BuyOrderQueue boq;
    Order oA1;
    Order oA2;
    Order oB1;

    private Order getTestStock(String stockSymbol, double price) {
        Order o = new Order(OrderType.BUY, "RANDOMID", stockSymbol, price, 10);
        return o;
    }

    @BeforeEach
    void beforeEach() {
        boq = new BuyOrderQueue();

        oA1 = getTestStock("A", 5.0);
        oA2 = getTestStock("A", 15.0);
        oB1 = getTestStock("B", 10.0);
    }

    @Test
    void testEnqueueAndGetSize() {
        boq.enqueue(oA1);
        assertEquals(1, boq.getSize());
        boq.enqueue(oA2);
        assertEquals(2, boq.getSize());
        boq.enqueue(oB1);
        assertEquals(3, boq.getSize());
    }

    @Test
    void testEnqueueAndGetSizeForStock() {
        boq.enqueue(oA1);
        assertEquals(1, boq.getSize(oA1.getStock()));

        boq.enqueue(oA2);
        assertEquals(2, boq.getSize(oA2.getStock()));

        boq.enqueue(oB1);
        assertEquals(1, boq.getSize(oB1.getStock()));
    }

    @Test
    void testEnqueueSellOrder() {
        Order o = new Order(OrderType.SELL, "id", "A", 10.0, 10);
        assertThrowsExactly(IllegalArgumentException.class, () -> boq.enqueue(o));
        assertEquals(0, boq.getSize());
    }

    @Test
    void testDequeue() {
        boq.enqueue(oA1);
        assertEquals(oA1, boq.dequeue(oA1.getStock()));
    }

    @Test
    void testDequeueOrder() {
        boq.enqueue(oA1);
        boq.enqueue(oA2);
        boq.enqueue(oB1);
        assertEquals(oA2, boq.dequeue(oA1.getStock()));
        assertEquals(oA1, boq.dequeue(oA1.getStock()));
        assertEquals(oB1, boq.dequeue(oB1.getStock()));
    }
}
