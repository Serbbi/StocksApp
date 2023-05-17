package nl.rug.aoop.messagequeue.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * Holds all of the data related to a message.
 */
@Slf4j
public class Message {
    private final String header;
    private final String value;
    private final LocalDateTime timestamp;

    /**
     * Main Constructor. Checks that value and header are not null.
     *
     * @param header    Key of the message.
     * @param value     Message value.
     * @param timestamp Timestamp of when the message was created.
     */
    public Message(String header, String value, LocalDateTime timestamp) {
        if (header == null) {
            throw new IllegalArgumentException("Header cannot be empty;");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be empty;");
        }
        this.header = header;
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * Generates timestamp on creation.
     *
     * @param header Key of the message.
     * @param value  Message value.
     */
    public Message(String header, String value) {
        this(header, value, LocalDateTime.now());
    }

    /**
     * Getter for timestamp.
     *
     * @return timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for message value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Getter for message key.
     *
     * @return key
     */
    public String getHeader() {
        return header;
    }

    /**
     * Creates the gson object using a gson builder.
     * Adds the local date time serializer and deserializer.
     *
     * @return Gson object to be used for converting a message to or from json.
     */
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        Gson gson = gsonBuilder.setPrettyPrinting().create();
        return gson;
    }

    /**
     * Produces a JSON string representation of the message.
     *
     * @return JSON String
     */
    public String toJSON() {
        Gson gson = getGson();
        String str = gson.toJson(this);
        return str;
    }

    /**
     * Produces a message object from a json string using gson.
     *
     * @param json JSON string to be converted to a message.
     * @return Instance of message from the json string.
     */
    public static Message fromJSON(String json) {
        Gson gson = Message.getGson();
        return gson.fromJson(json, Message.class);
    }
}
