package conexion;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase Connection que permite crear la conexión mediante sockets de un servidor y sus clientes, hereda de la clase
 * Thread, permitiendo a quien extienda de esta crear hilos para manejo de programación concurrente
 * @Author Jorge Luis Velasquez Venegas
 */
public class Connection extends Thread{

    /**
     * PORT: número de puerto por defecto en el cual escucha el servidor
     */
    private final int PORT = 2022;

    /**
     * HOST: dirección ip del servidor
     */
    private final String HOST = "localhost";

    /**
     * ServerSocket: socket de servidor, para crear servidor de sockets
     */
    protected ServerSocket serverSocket;

    /**
     * ClienteSocket: socket de clientes, para crear sockets de comunicación para los clientes
     */
    protected Socket clientSocket;

    /**
     * Constructor de la clase Connection, permite crear conexiones mediante sockets para servidor y clientes
     * @param type tipo de conexión a establecer(servidor o cliente)
     * @throws IOException
     */
    public Connection(String type) throws IOException{
        this.connection(type, 0, "");
    }

    /**
     * Constructor de la clase Connection, permite crear conexiones mediante sockets para servidor y clientes
     * @param type tipo de conexión a establecer(servidor o cliente)
     * @param port puerto en que escucha el servidor
     * @throws IOException
     */
    public Connection(String type, Integer port) throws IOException {
        this.connection(type, port, "");
    }

    /**
     * Constructor de la clase Connection, permite crear conexiones mediante sockets para servidor y clientes
     * @param type tipo de conexión a establecer(servidor o cliente)
     * @param port puerto en que escucha el servidor
     * @param host dirección ip del servidor
     * @throws IOException
     */

    public Connection(String type, Integer port, String host) throws IOException {
      this.connection(type, port, host);
    }

    /**
     * Permite crear la conexión de servidor o cliente mediante el uso de sockets
     * @param type tipo de conexión a establecer(servidor o cliente)
     * @param port puerto en que escucha el servidor
     * @param host dirección ip del servidor
     * @throws IOException
     */
    public void connection(String type, Integer port, String host) throws IOException {
        port =  port == null || port < 1024 ? PORT : port;
        host = host.isBlank() ? HOST : host;

        if (type.equalsIgnoreCase("servidor")){
            serverSocket = new ServerSocket(port);
            clientSocket = new Socket();
        }else{
            clientSocket = new Socket(host, port);

        }
    }
}
