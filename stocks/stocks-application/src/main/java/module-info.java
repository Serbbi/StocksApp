module stocksapplication {
    requires static lombok;
    requires org.slf4j;
    requires org.mockito;
    requires com.google.gson;
    requires networking;
    requires command;
    requires messagequeue;
    requires util;
    requires stock.market.ui;
    requires awaitility;
    opens nl.rug.aoop.stocksapplication.broadcast to com.google.gson;
    opens nl.rug.aoop.stocksapplication.order to com.google.gson;
    opens nl.rug.aoop.stocksapplication to com.google.gson, com.fasterxml.jackson.databind;
}