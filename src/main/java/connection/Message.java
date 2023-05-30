package connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Clase Message, permite establecer conexión con un servidor enviando un tipo de acción a ejecutar, ya sea de
 * configuración para informar conexión, desconexión o un mensaje nuevo a enviar a otro cliente en la lista de
 * destinatarios, implementa la interfaz Serializable por ser empleada para transmisión de datos a través de ObjetStream
 * @Author Jorge Luis Velasquez Venegas
 */
public class Message implements Serializable {

    /**
     * Type: tipo de acción a ejecutar (tipo de acción a realizar ejemplo: conexión aceptada "accept-connection",
     * nuevo cliente conectado "new-client", cliente desconectado "disconnect-client", mensaje "message"
     */
    private String type;

    /**
     * Destiny: cliente destinatario del mensaje
     */
    private String destiny;

    /**
     * Sender: emisor o remitente de un mensaje
     */
    private String sender;

    /**
     * Message: mensaje enviado para ser interpretado por un cliente destino
     */
    private String message;

    /**
     * Destinies: lista de clientes destino habilitados en el servidor para recibir mensajes
     */
    private ArrayList<String> destinies;

    /**
     * Constructor de la clase Message con cuatro parámetros usado cuando un cliente construye el objeto message para
     * enviar un mensaje a otro cliente
     * @param type tipo de acción a ejecutar (tipo de acción a realizar ejemplo: conexión aceptada "accept-connection",
     *             nuevo cliente conectado "new-client", cliente desconectado "disconnect-client", mensaje "message"
     * @param destiny cliente destinatario del mensaje
     * @param sender emisor o remitente de un mensaje
     * @param message mensaje enviado para ser interpretado por un cliente destino
     */
    public Message(String type, String destiny, String sender, String message) {
        this.type = type;
        this.destiny = destiny;
        this.sender = sender;
        this.message = message;
    }

    /**
     * Constructor con dos parámetros usados para construir un objeto message cuando se desea enviar una directiva de
     * configuración que deber ser procesada por el servidor como cuando se crea una nueva instancia de cliente y este
     * informa al servidor que su conexión sea aceptada ("connection-accept")
     * @param type tipo de acción a ejecutar (tipo de acción a realizar ejemplo: conexión aceptada "accept-connection",
     *             nuevo cliente conectado "new-client", cliente desconectado "disconnect-client", mensaje "message"
     * @param sender emisor o remitente de un mensaje
     */
    public Message(String type, String sender) {
        this.type = type;
        this.sender = sender;
    }

    /**
     * Retorna un String que indica el tipo de acción a ejecutar (tipo de acción a realizar ejemplo: conexión aceptada
     * "accept-connection", nuevo cliente conectado "new-client", cliente desconectado "disconnect-client",
     * mensaje "message")
     * @return un tipo de acción a ejecutar
     */
    public String getType() {
        return type;
    }

    /**
     * Establece la acción a ejecutar recibiendo un String (tipo de acción a realizar ejemplo: conexión aceptada
     * "accept-connection", nuevo cliente conectado "new-client", cliente desconectado "disconnect-client",
     * mensaje "message")
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retorna el nombre del cliente destinatario al que se le enviara el mensaje
     * @return retorna destinatario de mensaje
     */
    public String getDestiny() {
        return destiny;
    }

    /**
     * Retorna el emisor o remitente de un mensaje o acción de configuración
     * @return retorna el nombre del emisor o remitente
     */
    public String getSender() {
        return sender;
    }

    /**
     * Establece el nombre de un emisor o remitente de un mensaje o acción de configuración
     * @param sender nombre del emisor o remitente
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Retorna el mensaje a enviar o procesar
     * @return mensaje
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el mensaje a enviar o procesar
     * @param message mensaje a enviar o procesar
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retorna la lista de destinatarios habilitados en el servidor
     * @return lista de destinatarios habilitados en el servidor
     */
    public ArrayList<String> getDestinies() {
        return destinies;
    }

    /**
     * Carga la lista de destinatarios habilitados en el servidor
     * @param destinies lista de destinatarios habilitados en el servidor
     */
    public void setDestinies(ArrayList<String> destinies) {
        this.destinies = destinies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(type, message1.type)
                && Objects.equals(destiny, message1.destiny)
                && Objects.equals(sender, message1.sender)
                && Objects.equals(message, message1.message)
                && Objects.equals(destinies, message1.destinies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, destiny, sender, message, destinies);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", destiny='" + destiny + '\'' +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", destinies=" + destinies +
                '}';
    }
}
