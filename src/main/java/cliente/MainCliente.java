package cliente;

import java.io.IOException;
import java.util.Scanner;

public class MainCliente {

    public static void main(String[] args) throws IOException {

        System.out.println("Iniciando cliente");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese dirección ip de servidor (default= localhost): ");

        String ip = scanner.nextLine();

        System.out.print("Ingrese puerto de conexión (default= 2022): ");

        Integer port;
        try {
            port = Integer.valueOf(scanner.nextLine());
        } catch (NumberFormatException e) {
            port = 0;
        }


        System.out.print("Ingrese su nombre: ");

        String name = scanner.nextLine();

        Client client = new Client(ip, port, name);

        String message = "";

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int notClients = 0;
        while (client.isConnected()) {

            if (client.getDestinies() == null || client.getDestinies().size() == 0) {
                if (notClients == 0) System.out.println("No hay clientes conectados");
                notClients++;
            } else {
                System.out.println("Seleccione destinatario: ");
                for (int i = 0; i < client.getDestinies().size(); i++) {
                    System.out.println(i + 1 + ": " + client.getDestinies().get(i));
                }
                int numberDestiny = Integer.parseInt(scanner.nextLine()) - 1;
                String destiny = client.getDestinies().get(numberDestiny);

                System.out.print("Ingrese un mensaje: ");

                message = scanner.nextLine();

                client.sendMessage(message, destiny);
            }
        }

    }
}
