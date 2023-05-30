package cliente;

import javax.swing.*;

/**
 * Clase principal que inicia la ejecución del programa del lado cliente, permitiendo así crear todos los objetos
 * que dan vida a la aplicación, como lo son el objeto de tipo Client, los objetos de eventos del tipo
 * EventChangeClient y el objeto de interfaz gráfica del tipo WindowClient
 * @Author Jorge Luis Velasquez
 */
public class MainWindowClient {

    /**
     * Método main que da inicio a la ejecución del programa
     * @param args argumentos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WindowClient windowClient = new WindowClient();
                windowClient.setSize(400, 300);
                windowClient.setVisible(true);
            }
        });
    }
}
