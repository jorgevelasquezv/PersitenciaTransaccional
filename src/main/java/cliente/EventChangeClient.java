package cliente;

import java.util.EventObject;

public class EventChangeClient extends EventObject {

    Client client;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public EventChangeClient(Object source, Client client) {
        super(source);
        this.client = client;
    }
}
