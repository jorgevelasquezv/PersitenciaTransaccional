package cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class WindowClient extends JFrame {
    private JPanel panelMain;
    private JLabel nameClient;
    private JComboBox destinies;
    private JTextArea console;
    private JTextField message;

    private String messageActual = "";

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

    private Client client;


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
     * Ventana de configuración para la conexión
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

    private void eventSendMessage() {
        sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageToSend = message.getText();
                message.setText("");
                String destiny = (String) destinies.getSelectedItem();
                client.sendMessage(messageToSend, destiny);
                if (destiny!= null && !destiny.isBlank()) {
                    console.append(client.getIdClient() + " -> " + messageToSend + "\n");
                }
                if (!client.isConnected()){
                    System.exit(0);
                }
            }
        });
    }

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

    public void eventPrintMessage() {
        EventChangeClientListener clientPrintMessage = new EventChangeClientListener() {
            @Override
            void onMessageChange(EventChangeClient event) {
                if( client.getMessage() != null && !messageActual.equals(client.getMessage())){
                    messageActual = client.getMessage();
                    console.append(client.getSender() + " -> " + messageActual + "\n");
                }
            }
        };
        client.addEventListener(clientPrintMessage);
    }

    private void eventClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.closeConnection();
            }
        });
    }

}
