package nl.rug.aoop.command;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CommandTest {
    private Map<String, Object> map;

    @BeforeEach
    void setUp() {
        map = new HashMap<>();
        map.put("key", "value");
    }

    @Test
    void testRetrieveOption() {
        assertInstanceOf(String.class, Command.retrieveOption("key", String.class, map));
    }

    @Test
    void testRetrieveOptionOfIncorrectType() {
        assertThrows(IllegalArgumentException.class, () -> Command.retrieveOption("key", Integer.class, map));
    }
}