package servidor;

import java.util.EventListener;

/**
 * Interfaz que define los eventos personalizados para la clase Server, al percibir cambio del atributo
 * message a través del método modificador setter, extiende la interfaz EventListener por
 * obligatoriedad de escucha de eventos.
 * @Author Jorge Luis Velasquez
 */
public interface EventChangeServerListener extends EventListener {

    /**
     * Método abstracto para implementar lógica del manejo de evento al cambiar el atributo message de la clase Server
     * @param event evento ocurrido en el objeto padre
     */
    void onMessageChange(EventChangeServer event);
}
