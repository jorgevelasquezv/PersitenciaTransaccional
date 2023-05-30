package servidor;

import java.util.EventObject;

/**
 * Esta clase permite crear objetos de evento para la clase Server, asi monitorear los cambios en
 * sus atributos. Esta clase debe extender de EventObject, ya que es la clase raíz de la que se derivarán todos los
 * objetos de estado de evento.
 * @Author Jorge Luis Velasquez
 */
public class EventChangeServer extends EventObject {

    /**
     * Server: Atributo que permite recibir la instancia en la que sucede el evento
     */
    Server server;

    /**
     * Construye un prototipo de evento.
     *
     * @param source objeto en el que ocurrió inicialmente el evento
     * @param server objeto en el que ocurrió inicialmente el evento
     * @throws IllegalArgumentException if source is null
     */
    public EventChangeServer(Object source, Server server) {
        super(source);
        this.server = server;
    }
}
