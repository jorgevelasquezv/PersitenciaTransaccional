package servidor;

import javax.swing.*;

/**
 * Clase principal que inicia la ejecución del programa del lado servidor, permitiendo así crear todos los objetos
 * que dan vida a la aplicación, como lo son el objeto de tipo Server, los objetos de eventos del tipo
 * EventChangeServer y el objeto de interfaz gráfica del tipo WindowServer
 * @Author Jorge Luis Velasquez
 */
public class MainWindowServer {

    /**
     * Método main que da inicio a la ejecución del programa
     * @param args argumentos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WindowServer windowServer = new WindowServer();
                windowServer.setSize(300, 300);
                windowServer.setVisible(true);
            }
        });
    }
}
