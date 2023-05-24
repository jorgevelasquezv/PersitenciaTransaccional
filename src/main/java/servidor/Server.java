package servidor;

import conexion.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Clase Server para crear servidor de conexión mediante sockets, creando un chat bidireccional con clientes. Permanece
 * en escucha activa a la espera de objetos de la clase Message. Hereda de la clase Connection para establecer una
 * conexión empleando sockets, está a su vés hereda de la clase Thread lo cual permite generar hilos para manejar la
 * concurrencia del programa. Asi mientras la clase principal está atenta a la interfaz, se ejecuta un hilo que está
 * atento a los mensajes de los clientes
 * @Author Jorge Luis Velasquez Venegas
 */
public class Server extends Connection {

    private String messageConsole;

    /**
     * Listado de hilos para conexión de clientes habilitados y conectados al servidor
     */
    private ArrayList<ThreadClient> clients;

    /**
     * Listado de eventos a escuchar
     */
    private static ArrayList listeners;

    /**
     * Constructor de la clase Server para crear servidor de conexión mediante sockets, el servidor permanece en escucha
     * permanente de las solicitudes de los clientes que se conectan al servidor
     * @throws IOException
     */
    public Server() throws IOException {
        super("servidor");
        clients = new ArrayList<>();
        listeners = new ArrayList<>();
        this.start();
    }

    /**
     * Constructor de la clase Server para crear servidor de conexión mediante sockets, el servidor permanece en escucha
     * permanente de las solicitudes de los clientes que se conectan al servidor
     * @param port puerto en el que estará escuchando el servidor
     * @throws IOException
     */
    public Server(Integer port) throws IOException {
        super("servidor", port);
        clients = new ArrayList<>();
        listeners = new ArrayList<>();
        this.start();
    }

    /**
     * Método que se ejecuta al terminar de construir el objeto de la clase Server, el cual da inicio al hilo de
     * programación concurrente, habilitando la escucha permanente del servidor y creando un nuevo hilo de cliente, cada
     * vez que se conecta un cliente nuevo
     */
    @Override
    public void run() {
        try {
            System.out.println("Esperando....");
            while (true) {
                ThreadClient client;

                clientSocket = serverSocket.accept();

                client = new ThreadClient(clientSocket, this);
            }

        } catch (Exception e) {
            System.out.println("Error en run() server: " + e);
//            System.exit(0);
        }
    }

    /**
     * Retorna la lista de objetos hilos correspondiente a los clientes conectados al servidor
     * @return
     */
    public ArrayList<ThreadClient> getClients() {
        return clients;
    }

    /**
     * Agrega un objeto hilo de cliente a la lista de clientes conectados al servidor
     * @param client objeto de hilo de cliente
     */
    public void addClient(ThreadClient client) {
        this.clients.add(client);
    }

    /**
     * Elimina un objeto hilo de cliente de la lista de clientes conectados al servidor
     * @param client objeto de hilo de cliente
     */
    public void removeClient(ThreadClient client){
        this.clients.remove(client);
    }

    /**
     * Retorna el mensaje que se debe imprimir en la consola
     * @return String con mensaje que se debe mostrar en consola
     */
    public String getMessageConsole() {
        return messageConsole;
    }

    /**
     * Establece el valor del mensaje que se debe mostrar en consola
     * @param messageConsole String con el mensaje que se debe mostrar en consola
     */
    public void setMessageConsole(String messageConsole) {
        this.messageConsole = messageConsole;
        this.triggerMessageEvent();
    }

    /**
     * Agrega un evento al listado de eventos a escuchar
     * @param listener
     */
    public void addEventListener(EventChangeServerListener listener) {
        listeners.add(listener);
    }

    /**
     * Método para disparar el evento cuando cambie la variable message que contiene el mensaje que se debe mostrar
     * en consola
     */
    private void triggerMessageEvent() {

        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            EventChangeServerListener listener = (EventChangeServerListener) li.next();
            EventChangeServer event= new EventChangeServer(this, this);
            (listener).onMessageChange(event);
        }
    }
}
