package nl.rug.aoop.messagequeue.message;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Message to be sent over the network.
 */
@Slf4j
public class NetworkMessage {
    @Getter
    private final String header;
    @Getter
    private final String value;

    /**
     * NetworkMessage constructor.
     *
     * @param header header of the message.
     * @param value  value of the message.
     */
    public NetworkMessage(String header, String value) {
        this.header = header;
        this.value = value;
    }

    /**
     * Produces a JSON string representation of the message.
     *
     * @return JSON String
     */
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Produces a message object from a json string using gson.
     *
     * @param json JSON string to be converted to a message.
     * @return Instance of message from the json string.
     */
    public static NetworkMessage fromJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, NetworkMessage.class);
    }
}
