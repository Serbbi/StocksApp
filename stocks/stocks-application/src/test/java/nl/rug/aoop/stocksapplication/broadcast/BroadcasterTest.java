package nl.rug.aoop.stocksapplication.broadcast;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.networking.server.Server;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.StockPortfolio;
import nl.rug.aoop.util.stock.StocksLoader;
import nl.rug.aoop.util.trader.Trader;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Slf4j
public class BroadcasterTest {
    @Test
    void testSendBroadcast() {
        UUID uuid = UUID.randomUUID();
        Map<String, UUID> clientHandlers = new HashMap<>();
        clientHandlers.put("id", uuid);

        Server server = Mockito.mock(Server.class);

        // Load stocks and traders from file to reduce manual typing out...
        Map<String, Stock> stocks = StocksLoader.getMapFromYaml("stocks");
        assertNotNull(stocks);
        Map<String, Trader> traders = new HashMap<>();

        Trader trader = new Trader("T1", "id",100, new ArrayList<>(), new StockPortfolio());
        traders.put(trader.getId(), trader);
        Broadcaster broadcaster = new Broadcaster(server, stocks, traders, clientHandlers);
        broadcaster.sendBroadcast();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UUID> captor1 = ArgumentCaptor.forClass(UUID.class);
        verify(server).sendResponse(captor1.capture(), captor.capture());

        assertEquals(captor1.getValue(),uuid);
        NetworkMessage msg = NetworkMessage.fromJSON(captor.getValue());
        assertEquals("broadcast", msg.getHeader());
        log.info("Broadcast Sent: " + captor.getValue());
    }

    @Test
    void testSendCustomMessage() {
        UUID uuid = UUID.randomUUID();
        Map<String, UUID> clientHandlers = new HashMap<>();
        clientHandlers.put("id", uuid);

        Map<String, Stock> stocks = new HashMap<>();
        Map<String, Trader> traders = new HashMap<>();
        Server server = Mockito.mock(Server.class);
        Broadcaster broadcaster = new Broadcaster(server, stocks, traders, clientHandlers);

        String traderID = "ID";
        String message = "custommessage";
        broadcaster.sendCustomMessage(traderID, message);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(server).sendResponse(any(), captor.capture());
        NetworkMessage networkMessage = NetworkMessage.fromJSON(captor.getValue());
        assertEquals(networkMessage.getHeader(),"notification");
        assertEquals(networkMessage.getValue(),message);
    }

    @Test
    void testGetJsonBroadcastMessage() {
        UUID uuid = UUID.randomUUID();
        Map<String, UUID> clientHandlers = new HashMap<>();
        clientHandlers.put("id", uuid);

        Trader trader = new Trader("T1", "id",100, new ArrayList<>(), new StockPortfolio());
        Map<String, Stock> stocks = new HashMap<>();
        Map<String, Trader> traders = new HashMap<>();
        Server server = Mockito.mock(Server.class);
        Broadcaster broadcaster = new Broadcaster(server, stocks, traders, clientHandlers);

        String jsonmessage = broadcaster.getJsonBroadcastMessage(trader);
        NetworkMessage networkMessage = NetworkMessage.fromJSON(jsonmessage);
        assertEquals(networkMessage.getHeader(),"broadcast");
        log.info("Broadcast Sent: " + networkMessage.getValue());
    }

    @Test
    void testSendResponseToASingleTrader() {
        UUID uuid = UUID.randomUUID();
        Map<String, UUID> clientHandlers = new HashMap<>();
        clientHandlers.put("id", uuid);

        Trader trader = new Trader("T1", "id",100, new ArrayList<>(), new StockPortfolio());
        Map<String, Stock> stocks = new HashMap<>();
        Map<String, Trader> traders = new HashMap<>();
        Server server = Mockito.mock(Server.class);
        Broadcaster broadcaster = new Broadcaster(server, stocks, traders, clientHandlers);
        broadcaster.sendBroadcast(trader.getId());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(server).sendResponse(any(), captor.capture());
        NetworkMessage msg = NetworkMessage.fromJSON(captor.getValue());
        assertEquals("broadcast", msg.getHeader());
        log.info("Broadcast Sent: " + captor.getValue());
    }
}
