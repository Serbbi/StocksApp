module util {
    requires static lombok;
    requires org.slf4j;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires stock.market.ui;
    requires command;
    exports nl.rug.aoop.util;
    exports nl.rug.aoop.util.trader;
    exports nl.rug.aoop.util.stock;
    exports nl.rug.aoop.util.order;
    opens nl.rug.aoop.util to com.google.gson, com.fasterxml.jackson.databind;
    opens nl.rug.aoop.util.order to com.google.gson, com.fasterxml.jackson.databind;
    opens nl.rug.aoop.util.trader to com.google.gson, com.fasterxml.jackson.databind;
    opens nl.rug.aoop.util.stock to com.google.gson, com.fasterxml.jackson.databind;
}