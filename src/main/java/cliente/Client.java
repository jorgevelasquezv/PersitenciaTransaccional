package cliente;

import connection.Connection;
import connection.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * La clase Client permite crear objetos cliente que se conectan mediante sockets con el servidor para entablar el
 * chat bidireccional. Esta clase hereda de la clase Connection para establecer una conexión empleando sockets, la
 * clase Connection a su vez hereda de la clase Thread, lo cual permite generar hilos para manejar la concurrencia
 * del programa. Así mientras la clase principal está atenta a la interfaz, se ejecuta un hilo que se encarga de
 * enviar y recibir mensajes del servidor
 * @Author Jorge Luis Velasquez Venegas
 */
public class Client extends Connection {

    /**
     * IpAddress: dirección ip del servidor al que se desea conectar
     */
    private String ipAddress;

    /**
     * IP_ADDRESS: dirección ip del servidor que se establece por defecto cuando se crea una instancia de la clase
     * cliente sin asignar el valor
     */
    private final String IP_ADDRESS = "localhost";

    /**
     * Port: puerto de conexión con el servidor este debe coincidir con el puerto en el que escucha el servidor
     */
    private Integer port;

    /**
     * PORT: puerto por defecto para conexión con el servidor
     */
    private final Integer PORT = 2022;

    /**
     * IdClient: nombre que identifica al cliente en la conexión
     */
    private String idClient;

    /**
     * Nombre de emisor o remitente de mensaje
     */
    private String sender;

    /**
     * Destinies: lista de destinatarios habilitados en el servidor
     */
    private ArrayList<String> destinies;

    /**
     * ObjectInputStream: recepción en Stream de Objetos de la clase Message
     */
    private ObjectInputStream objectInputStream;

    /**
     * ObjectOutputStream: envío en Stream de Objetos de la clase Message
     */
    private ObjectOutputStream objectOutputStream;

    /**
     * Connected: indica si el cliente se encuentra activo o conectado para escuchar transmisiones
     */
    private boolean connected;

    /**
     * Message: string con el mensaje recibido
     */
    private String message;

    /**
     * Listado de eventos a escuchar
     */
    private static ArrayList listeners;

    /**
     * Constructor de la clase Client para crear instancias de client con sockets y crear un chat bidireccional con el
     * servidor mediante sockets, se ejecuta el método start al terminar de construir el objeto para asi iniciar el
     * hilo de ejecución concurrente
     * @param ipAddress Dirección ip del servidor
     * @param port puerto en que escucha el servidor
     * @param idClient nombre que identifica la cliente
     * @throws IOException
     */
    public Client(String ipAddress, Integer port, String idClient) throws IOException {
        super("cliente", port, ipAddress);
        this.ipAddress = ipAddress.isBlank() ? IP_ADDRESS : ipAddress;
        this.port = port == null || port < 1024 ? PORT : port;
        this.idClient = idClient;
        listeners = new ArrayList<>();
        this.start();
    }

    /**
     * Método que se ejecuta al terminar de construir el objeto de la clase Client, el cual da inicio al hilo de
     * programación concurrente
     */
    @Override
    public void run() {
        try {
            objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
//            Se solicita conexión
            Message messageObject = new Message("connect", this.idClient);
            objectOutputStream.writeObject(messageObject);
//            Se queda en escucha permanente
            this.connected=true;
            this.listen();
        } catch (IOException e) {
            System.out.println("Error de conexión con el servidor");
            this.closeConnection();
        }
    }

    /**
     * Envía un mensaje a otro cliente habilitado en el servidor
     * @param message mensaje a enviar
     * @param destiny nombre de cliente destino
     */
    public void sendMessage(String message, String destiny) {
        try {
            if (message.equalsIgnoreCase("chao")) {
                this.closeConnection();
            } else if(destiny != null && !destiny.isBlank()) {
                Message messageObject = new Message("message", destiny, this.getIdClient(), message);
                objectOutputStream.writeObject(messageObject);
            }
        } catch (IOException e) {
            System.out.println("Error enviando mensaje " + e);
        }
    }

    /**
     * Loop para escucha activa de mensajes enviados desde el servidor
     */
    public void listen() {
        while (connected) {
            try {
                Object request = objectInputStream.readObject();
                if (request != null && request instanceof Message) {
                    Message message = (Message) request;
                    this.operations(message);
                } else {
                    System.out.println("Se recibió un valor inesperado");
                }
            } catch (ClassNotFoundException | IOException e) {
                if (!clientSocket.isClosed()) System.out.println("error en listen() de cliente " + e);
            }
        }
    }

    /**
     * Ejecuta la acción determinada en el atributo type del objeto message (conexión aceptada "connection-accept",
     * nuevo cliente "new-client", cliente desconectado "disconnect-client", mensaje "message")
     * @param message objeto que contiene la acción a ejecutar y los datos a procesar
     */
    private void operations(Message message) {
        switch (message.getType()) {
            case "connection-accept":
                this.setDestinies(message.getDestinies());
                break;
            case "new-client":
                this.addDestiny(message.getSender());
                break;
            case "disconnect-client":
                this.removeDestiny(message.getSender());
                break;
            case "message":
                this.sender = message.getSender();
                this.setMessage(message.getMessage());
                System.out.println(this.sender + " -> " + this.message);
                break;
            default:
                break;
        }
    }

    /**
     * Notifica el servidor del cierre de conexión del socket y efectúa el cierre del socket
     */
    public void closeConnection() {
        try {
            this.connected = false;
            Message message = new Message("disconnect", this.idClient);
            objectOutputStream.writeObject(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.objectInputStream.close();
            this.objectOutputStream.close();
            clientSocket.close();

        } catch (IOException e) {
            System.out.println("Error cerrando conexión: " + e);
        }
    }

    /**
     * Retorna la lista de clientes destino habilitados en el servidor
     * @return lista de clientes destino
     */
    public ArrayList<String> getDestinies() {
        return destinies;
    }

    /**
     * Adiciona un cliente destino a la lista de clientes habilita en el servidor
     * @param destiny destinatario a agregar en la lista de clientes destino
     */
    public void addDestiny(String destiny) {
        this.destinies.add(destiny);
        this.triggerDestiniesEvent();
    }

    /**
     * Elimina un cliente destino a la lista de clientes habilita en el servidor
     * @param destiny destinatario a eliminar en la lista de clientes destino
     */
    public void removeDestiny(String destiny) {
        if (this.destinies != null && !this.destinies.isEmpty()) {
            this.destinies.remove(destiny);
            this.triggerDestiniesEvent();
        }
    }

    /**
     * Agrega un evento al listado de eventos a escuchar
     * @param listener evento a agregar en la lista de eventos a escuchar
     */
    public void addEventListener(EventChangeClientListener listener) {
        listeners.add(listener);
    }

    /**
     * Método para disparar el evento cuando cambie la variable message que contiene el mensaje que se debe mostrar
     * en consola
     */
    private void triggerMessageEvent() {

        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            EventChangeClientListener listener= (EventChangeClientListener) li.next();
            EventChangeClient event= new EventChangeClient(this, this);
            (listener).onMessageChange(event);
        }
    }

    /**
     * Método para disparar el evento cuando cambie la variable destinies que contiene la lista de destinatarios
     * habilitados en el servidor
     */
    private void triggerDestiniesEvent() {

        ListIterator li = listeners.listIterator();
        while (li.hasNext()) {
            EventChangeClientListener listener= (EventChangeClientListener) li.next();
            EventChangeClient event= new EventChangeClient(this, this);
            (listener).onDestiniesChange(event);
        }
    }


    /**
     * Carga la lista de clientes destino habilita en el servidor
     * @param destinies
     */
    public void setDestinies(ArrayList<String> destinies) {
        this.destinies = destinies;
        this.triggerDestiniesEvent();
    }

    /**
     * Retorna string que contiene el nombre que identifica al cliente
     * @return nombre que identifica al cliente
     */
    public String getIdClient() {
        return idClient;
    }

    /**
     * Retorna estado en que se encuentra el cliente para escuchar mensajes
     * @return estado de conexión
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Retorna el mensaje recibido
     * @return String con mensaje recibido
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el valor del mensaje recibido
     * @param message String que contiene el mensaje recibido
     */
    public void setMessage(String message) {
        this.message = message;
        this.triggerMessageEvent();
    }

    /**
     * Retorna el nombre o identificador del remitente de un mensaje recibido
     * @return String que identifica al remitente de un mensaje
     */
    public String getSender() {
        return sender;
    }
}
