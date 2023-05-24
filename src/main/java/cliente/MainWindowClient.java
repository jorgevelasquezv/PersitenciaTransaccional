package cliente;

import javax.swing.*;

public class MainWindowClient {

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
