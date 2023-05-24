package servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
     * Constructor de la clase WindowsServer crea una ventana para visualizar estado de servidor y cerrar conexión
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
     * Ventana de configuración para la conexión
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
     * Método para imprimir datos en consola de servidor
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
     * Método que agrega evento al botón salir para terminar conexión y cerrar programa
     */
    private void closeConnection() {
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.getClients().forEach(ThreadClient::endConnection);
            }
        });
    }

}
