package servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Clase que permite crear la ventana gráfica empleando Javax Swing, Extiende de JFrame para ser usado como contenedor
 * principal y heredar todos los métodos para implementación de interfaz gráfica.
 * En esta clase se genera una primera ventana de tipo dialog para establecer la configuración puerto del servidor
 * socket. Cuando se es aceptada la configuración, se genera una segunda ventana de tipo JFrame en la cual se
 * visualiza cuando un cliente se conecta o desconecta. El uso de eventos permite la actualización en segundo plano del
 * TextArea en el que se visualiza cuando un cliente se conecta o desconecta
 * @Author Jorge Luis Velasquez
 */
public class WindowServer extends JFrame {

    /**
     * Console: instancia de JTextArea para visualizar en ventana el estado del servidor
     */
    private JTextArea console;

    /**
     * PanelMain: instancia de JPanel empleado como contenedor principal de los demás elementos de la ventana
     */
    private JPanel panelMain;

    /**
     * Exit: botón para cerrar conexión y terminar programa
     */
    private JButton exit;

    /**
     * Server: instancia de la clase server para obtener todos los métodos y atributos del servidor
     */
    private Server server;

    /**
     * MessageActual: String que contiene el mensaje actual que se visualiza en Text Área como estado del servidor
     */
    private String messageActual;

    /**
     * Constructor de la clase WindowServer. Crea los objetos para visualización gráfica de ventana de configuración,
     * objeto de la clase Server para establecer iniciar el servidor en escucha activa, ventana principal de interacción
     * de servidor y los eventos necesarios para la actualización en segundo plano de la consola de mensajes enviados y
     * recibidos, además el evento del mouse al hacer click, se realizan estas acciones a través de métodos
     */
    public WindowServer() {
        super("Cliente de chat bidireccional con sockets");
        setUp();
        setContentPane(panelMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        printDataIntoConsole();
        closeConnection();

    }

    /**
     * Método que permite crear la ventana de configuración para la conexión
     */
    private void setUp() {

        JPanel windowConfiguration = new JPanel(new GridLayout(3, 2));

        windowConfiguration.add(new JLabel("Puerto de conexión:"));
        JFormattedTextField port = new JFormattedTextField(2022);
        windowConfiguration.add(port);

        int option = JOptionPane.showConfirmDialog(null, windowConfiguration,
                "Configuraciones de puerto de servidor", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                this.server = new Server((Integer) port.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.exit(0);
        }
    }

    /**
     * Método para manejar el evento que se genera en el objeto de clase Server cuando se recibe una nueva conexión o
     * se solicita desconexión y asi actualizar el área multi línea donde se visualizan los mensajes enviados y
     * recibidos en segundo plano
     */
    private void printDataIntoConsole() {
        EventChangeServerListener messageConsoleListener = new EventChangeServerListener() {
            @Override
            public void onMessageChange(EventChangeServer evt) {
                String messageServer = server.getMessageConsole();
                if (messageServer != null && !messageServer.equals(messageActual)) {
                    console.append(server.getMessageConsole());
                    messageActual = messageServer;
                }
            }
        };
        server.addEventListener(messageConsoleListener);

    }

    /**
     * Método para manejar el cierre de la ventana al oprimir el botón de cerrar o finalizar
     */
    private void closeConnection() {
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.getClients().forEach(ThreadClient::endConnection);
                System.exit(0);
            }
        });
    }
}
