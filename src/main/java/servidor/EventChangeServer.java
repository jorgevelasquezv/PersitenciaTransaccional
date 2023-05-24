package servidor;

import java.util.EventObject;

public class EventChangeServer extends EventObject {

    Server server;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public EventChangeServer(Object source, Server server) {
        super(source);
        this.server = server;
    }
}
