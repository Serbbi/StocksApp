package nl.rug.aoop.util.trader;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.stock.StocksLoader;
import nl.rug.aoop.util.YamlLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.lang.String.format;

/**
 * TraderLoader to load traders from yaml file.
 */
@Slf4j
public class TradersLoader {
    /**
     * Loads a map from a YAML file.
     *
     * @param name Name of resource to load.
     * @return Map containing the data loaded from the yaml file.
     */
    public static Map<String, Trader> getMapFromYaml(String name) {
        log.info("Loading " + name + " from " + name + ".yaml");
        InputStream resource = StocksLoader.class.getClassLoader().getResourceAsStream(name + ".yaml");

        if (resource == null) {
            log.info(format("%s.yaml does not exist. Loading without it...", name));
            return new HashMap<>();
        }

        try {
            YamlLoader yamlLoader = new YamlLoader(resource);
            List<Trader> list = Arrays.stream(yamlLoader.load(Trader[].class)).toList();

            Map<String, Trader> traders = new HashMap<>();

            list.forEach(trader -> {
                traders.put(trader.getId(), trader);
            });

            log.info((traders.size()) + " " + name + " loaded!");
            return traders;
        } catch (IOException e) {
            log.error("Failed to load " + name + " from YAML! (IOException)", e);
        }
        // Only reached on error.
        return new HashMap<>();
    }
}
