package cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Clase que permite crear la ventana gráfica empleando Javax Swing, Extiende de JFrame para ser usado como contenedor
 * principal y heredar todos los métodos para implementación de interfaz gráfica.
 * En esta clase se genera una primera ventana de tipo dialog para establecer la configuración de conexión con el
 * servidor socket estableciendo el Host o Ip y el número puerto de conexión del servidor. Cuando se es aceptada la
 * configuración, se genera una segunda ventana de tipo JFrame en la cual se visualizan los clientes conectados y los
 * mensajes enviados y recibidos, además de encontrar la opción de enviar mensajes seleccionando alguno de los clientes
 * conectados. El uso de eventos permite la actualización en segundo plano de la lista de clientes a medida que se
 * conectan o desconectan. La lista es visualizada en un JComboBox. El uso de eventos generados en la clase Client
 * permiten la actualización en segundo plano del TextArea en el que se visualizan los mensajes enviados y recibidos
 * @Author Jorge Luis Velasquez
 */
public class WindowClient extends JFrame {
    /**
     * PanelMain: Panel principal de tipo JPanel en el cual se encuentran contenidos los demás elementos de la venta
     */
    private JPanel panelMain;

    /**
     * NameClient: etiqueta del tipo JLabel, la cual permite visualizar en la parte superior central de la ventana el
     * nombre o alias asignado por el cliente en la ventana de configuración de conexión
     */
    private JLabel nameClient;

    /**
     * Destinies: lista de destinatarios conectados al servidor. Se visualiza en un JComboBox (lista desplegable), la
     * cual se actualiza en segundo plano gracias a los eventos creados para los atributos de la clase Client
     */
    private JComboBox destinies;

    /**
     * Console: Área multi línea de tipo JTextArea que permite visualizar los mensajes recibidos y enviados. Se
     * actualiza en segundo plano gracias a los eventos creados para los atributos de la clase Client
     */
    private JTextArea console;

    /**
     * Message: entrada de texto de tipo JTextField, permite el ingreso del mensaje que se desea enviar a otro
     * cliente conectado al servidor
     */
    private JTextField message;

    /**
     * SendMessage: Botón del tipo JButton. Permite ejecutar el método de envío de mensajes de la clase Client
     * empleando el evento del click del mouse sobre el botón
     */
    private JButton sendMessage;

    /**
     * IP_ADDRESS: dirección ip del servidor que se establece por defecto cuando se crea una instancia de la clase
     * cliente sin asignar el valor
     */
    private final String IP_ADDRESS = "127.0.0.1";

    /**
     * PORT: puerto por defecto para conexión con el servidor
     */
    private final String PORT = "2022";

    /**
     * Client: instancia del tipo Client la cual permite crear un cliente para comunicación con sockets
     */
    private Client client;

    /**
     * Constructor de la clase WindowClient. Crea los objetos para visualización gráfica de ventana de configuración,
     * objeto de la clase Client para establecer la conexión con el servidor, ventana principal de interacción de
     * cliente con el chat y los eventos necesarios para la actualización en segundo plano de lista de destinatarios
     * (clientes activos) y la consola de mensajes enviados y recibidos, además el evento del mouse al hacer click, se
     * realizan estas acciones a través de métodos
     */
    public WindowClient() {
        super("Cliente de chat bidireccional con sockets");
        setUp();
        setContentPane(panelMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        eventSendMessage();
        eventAddItemComboBox();
        eventPrintMessage();
        eventClose();

    }

    /**
     * Método que permite crear la ventana de configuración para la conexión
     */
    private void setUp() {

        JPanel configurationWindow = new JPanel(new GridLayout(3, 2));

        configurationWindow.add(new JLabel("IP del Servidor:"));
        JTextField ip = new JTextField(IP_ADDRESS, 20);
        configurationWindow.add(ip);

        configurationWindow.add(new JLabel("Puerto de conexión:"));
        JFormattedTextField port = new JFormattedTextField(2022);
        configurationWindow.add(port);

        configurationWindow.add(new JLabel("Ingrese su nombre o alias:"));
        JTextField userName = new JTextField("Usuario", 20);
        configurationWindow.add(userName);

        int option = JOptionPane.showConfirmDialog(null, configurationWindow,
                "Configuraciones de la comunicación", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String ipAddress = ip.getText();
            Integer portHost = (Integer) port.getValue();
            String idClient = userName.getText();
            nameClient.setText(userName.getText());
            try {
                this.client = new Client(ipAddress, portHost, idClient);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.exit(0);
        }
    }

    /**
     * Método para manejar el evento del click del mouse sobre el botón de enviar mensaje
     */
    private void eventSendMessage() {
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageToSend = message.getText();
                message.setText("");
                String destiny = (String) destinies.getSelectedItem();
                client.sendMessage(messageToSend, destiny);
                if (destiny!= null && !destiny.isBlank()) {
                    console.append("**** Mensaje enviado ****\n");
                    console.append(client.getIdClient() + " -> " + messageToSend + "\n");
                }
                if (!client.isConnected()){
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Método para manejar el evento que se genera en el objeto de clase Client cuando se agrega o elimina un cliente
     * destino de la lista de destinatarios y asi actualizar la lista desplegable en segundo plano
     */
    private void eventAddItemComboBox() {
        EventChangeClientListener clientAddItemComboBox = new EventChangeClientListener() {
            @Override
            public void onDestiniesChange(EventChangeClient event) {
                if (client.getDestinies() != null && !client.getDestinies().isEmpty()) {
                    destinies.removeAllItems();
                    client.getDestinies().forEach(destinies::addItem);
                    destinies.setSelectedIndex(0);
                }else{
                    destinies.removeAllItems();
                }
            }
        };
        client.addEventListener(clientAddItemComboBox);
    }

    /**
     * Método para manejar el evento que se genera en el objeto de clase Client cuando se recibe un mensaje y asi
     * actualizar el área multi línea donde se visualizan los mensajes enviados y recibidos en segundo plano
     */
    public void eventPrintMessage() {
        EventChangeClientListener clientPrintMessage = new EventChangeClientListener() {
            @Override
            void onMessageChange(EventChangeClient event) {
                console.append("**** Mensaje recibido ****\n");
                console.append(client.getSender() + " -> " +  client.getMessage() + "\n");
            }
        };
        client.addEventListener(clientPrintMessage);
    }

    /**
     * Método para manejar el cierre de la ventana al oprimir el botón de cerrar o finalizar
     */
    private void eventClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.closeConnection();
            }
        });
    }
}
