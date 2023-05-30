package cliente;

import java.util.EventObject;

/**
 * Esta clase permite crear objetos de evento para la clase Client, asi monitorear los cambios en
 * sus atributos. Esta clase debe extender de EventObject, ya que es la clase raíz de la que se derivarán todos los
 * objetos de estado de evento.
 * @Author Jorge Luis Velasquez
 */
public class EventChangeClient extends EventObject {

    /**
     * Client: Atributo que permite recibir la instancia en la que sucede el evento
     */
    Client client;

    /**
     * Construye un prototipo de evento.
     *
     * @param source objeto en el que ocurrió inicialmente el evento
     * @param client objeto en el que ocurrió inicialmente el evento
     * @throws IllegalArgumentException if source is null
     */
    public EventChangeClient(Object source, Client client) {
        super(source);
        this.client = client;
    }
}
