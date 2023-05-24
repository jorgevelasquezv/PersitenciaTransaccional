package servidor;

import javax.swing.*;

public class MainWindowServer {

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
