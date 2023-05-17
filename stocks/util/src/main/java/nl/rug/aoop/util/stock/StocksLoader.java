package nl.rug.aoop.util.stock;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.YamlLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * Utility class for loading data from files.
 */
@Slf4j
public class StocksLoader {
    /**
     * Loads a map from a YAML file.
     *
     * @param name Name of resource to load.
     * @return Map containing the data loaded from the yaml file.
     */
    public static Map<String, Stock> getMapFromYaml(String name) {
        log.info("Loading " + name + " from " + name + ".yaml");
        InputStream resource = StocksLoader.class.getClassLoader().getResourceAsStream(name + ".yaml");

        if (resource == null) {
            log.info(format("%s.yaml does not exist. Loading without it...", name));
            return new ConcurrentHashMap<>();
        }

        try {
            YamlLoader yamlLoader = new YamlLoader(resource);
            Map linkedHashMap = yamlLoader.load(HashMap.class);

            log.info((linkedHashMap.size()) + " " + name + " loaded!");
            Map<String, Stock> stocksMap = new ConcurrentHashMap<>();
            linkedHashMap.forEach((key, value) -> {
                stocksMap.put((String) key, createStock(value));
            });

            return stocksMap;
        } catch (IOException e) {
            log.error("Failed to load " + name + " from YAML! (IOException)");
        }
        // Only reached on error.
        return new ConcurrentHashMap<>();
    }

    /**
     * Creates a stock from the object that is returned from theh YAML loader.
     *
     * @param value Value from which to get the data.
     * @return The stock.
     */
    private static Stock createStock(Object value) {
        Map val = (Map) value;

        Long sharesOutstanding;
        if (val.get("sharesOutstanding").getClass() == Long.class) {
            sharesOutstanding = (long) val.get("sharesOutstanding");
        } else if (val.get("sharesOutstanding").getClass() == Integer.class) {
            sharesOutstanding = Long.valueOf((int) val.get("sharesOutstanding"));
        } else {
            throw new IllegalArgumentException("sharesOutstanding must be Long or Integer");
        }

        return new Stock((String) val.get("symbol"), (String) val.get("name"), sharesOutstanding,
                (double) val.get("initialPrice"));
    }

}
