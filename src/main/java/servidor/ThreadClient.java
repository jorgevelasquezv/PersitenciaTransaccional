package servidor;


import connection.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Clase ThreadClient permite crear sockets del lado del servidor para cada cliente conectado a este, y de este modo
 * cada socket creado trabaja en un hilo diferente para escuchar los mensajes enviados por los clientes, y así
 * establecer la comunicación en el chat bidireccional entre los clientes conectados al servidor. Hereda de la clase
 * Thread lo cual permite crear hilos habilitando la programación concurrente
 * @Author Jorge Luis Velasquez Venegas
 */
public class ThreadClient extends Thread {

    /**
     * Socket: punto final para la comunicación entre clientes.
     */
    private Socket socket;

    /**
     * ObjectInputStream: recepción en Stream de Objetos de la clase Message
     */
    private ObjectInputStream dataInputStream;

    /**
     * ObjectOutputStream: envío en Stream de Objetos de la clase Message
     */
    private ObjectOutputStream dataOutputStream;

    /**
     * Server: instancia de la clase servidor para manejo de los datos de comunicación
     */
    private Server server;

    /**
     * IdClient: nombre que identifica el cliente que se encuentra conectado
     */
    private String idClient;

    /**
     * Connected: estado en que se encuentra el cliente (conectado, desconectado)
     */
    private boolean connected;

    /**
     * Constructor de la clase ThreadClient crea una conexión del lado del servidor mediante sockets para la escucha de
     * clientes conectados al servidor
     * @param socket punto final para la comunicación entre clientes.
     * @param server instancia de la clase servidor para manejo de los datos de comunicación
     */
    public ThreadClient(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            dataInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        this.start();
    }

    /**
     * Método que se ejecuta al terminar de construir el objeto de la clase ThreadClient, el cual da inicio al hilo de
     * programación concurrente para la escucha activa del lado del servidor
     */
    @Override
    public void run() {
        try{
            listen();
        }catch (Exception e){
            System.out.println("Error al escuchar cliente " + e);
        }
    }

    /**
     * Loop de escucha activa de mensajes enviados por el cliente
     */
    public void listen(){
        connected = true;
        while (connected){
            try {
                Message message = (Message) dataInputStream.readObject();
                this.operations(message);
            } catch (IOException e) {
                System.out.println("Error en listen() HiloCliente: " + e.getMessage());
                this.notifyDisconnection(new Message("disconnect", this.idClient));
            } catch (ClassNotFoundException e) {
                System.out.println("Error en listen() HiloCliente error de datos recibidos: " + e.getMessage());
            }
        }
    }

    /**
     * Ejecuta la acción determinada en el atributo type del objeto message (confirmar conexión de un nuevo cliente
     * "connect", confirmar desconexión de un cliente "disconnect", enviar mensaje a un destinatario "message")
     * @param message objeto que contiene la acción a ejecutar y los datos a procesar
     */
    private void operations(Message message) {

        switch (message.getType()){
            case "connect":
                this.notifyConnection(message);
                break;
            case "disconnect":
                this.notifyDisconnection(message);
                break;
            case "message":
                String destiny = message.getDestiny();
                server.getClients()
                        .stream()
                        .filter(client -> destiny.equalsIgnoreCase(client.getIdClient()))
                        .forEach(client -> client.sendMessage(message));
                break;
            default:
                break;
        }
    }

    /**
     * Se confirma conexión establecida co servidor y se envía mensaje a todos los clientes conectados informando de un
     * nuevo cliente conectado y se agrega el nuevo cliente a la lista de clientes destino conectados y habilitados
     * para recibir mensajes
     * @param message objeto que contiene el nombre que identifica el nuevo cliente conectado
     */
    private void notifyConnection(Message message) {
        this.idClient = message.getSender();
        ArrayList<String> destines = server.getClients()
                .stream()
                .map(ThreadClient::getIdClient)
                .collect(Collectors.toCollection(ArrayList::new));

//      Se envía mensaje de confirmación de conexión aceptada
        message.setType("connection-accept");
        message.setDestinies(destines);
        this.sendMessage(message);

//      Pendiente cargar textos de salida en pantalla en una variable log
        server.setMessageConsole("Nuevo cliente conectado: " + idClient + "\n");
        System.out.println("Nuevo cliente conectado: " + idClient);

//      Se notifica a todos los clientes de un nuevo cliente conectado
        message.setType("new-client");
        server.getClients().forEach(client -> {
            message.setDestinies(
                    destines
                    .stream()
                    .filter(destine -> !client.getIdClient().equalsIgnoreCase(destine))
                    .collect(Collectors.toCollection(ArrayList::new)));
            client.sendMessage(message);
        });

//      Se agrega cliente nuevo al listado de clientes en servidor
        server.addClient(this);
    }

    /**
     * Se confirma desconexión de cliente con el servidor y se envía mensaje a todos los clientes conectados informando
     * de un nuevo cliente desconectado y se elimina el cliente de la lista de clientes destino
     * @param message objeto que contiene el nombre que identifica el cliente que se desconectara
     */
    private void notifyDisconnection(Message message) {
        server.setMessageConsole("Cliente " + message.getSender() + " desconectado\n");
        System.out.println("Cliente " + message.getSender() + " desconectado");


        server.removeClient(this);

//      Se notifica a todos los clientes de un cliente desconectado
        ArrayList<String> destinies = server.getClients()
                .stream()
                .filter(client -> !idClient.equalsIgnoreCase(client.getIdClient()))
                .map(ThreadClient::getIdClient).collect(Collectors.toCollection(ArrayList::new));

        message.setType("disconnect-client");
        message.setDestinies(destinies);
        server.getClients()
                .stream()
                .filter(client -> !idClient.equalsIgnoreCase(client.getIdClient()))
                .forEach(client -> client.sendMessage(message));
//        Retardo de 1 segundo para permitir envío de mensaje de desconexión antes de cerrar el socket
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.endConnection();
    }

    /**
     * Envío de mensajes a cliente en el otro extremo del socket
     * @param message
     */
    private void sendMessage(Message message) {
        try {
            dataOutputStream.writeObject(message);
        } catch (IOException e) {
            System.out.println("Error enviando mensaje " + e + " - " + this.getIdClient());
        }
    }

    /**
     * Se indica que el cliente está desconectado para finalizar el loop y se cierra conexión del socket del lado del
     * servidor
     */
    public void endConnection(){
        try {
            socket.close();
            connected = false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Retorna el nombre que identifica al cliente
     * @return nombre del cliente
     */
    public String getIdClient() {
        return idClient;
    }

}
