package network;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            Socket socket = new Socket(host, port);
            Player player = new Player(socket, -1); // Player ID will be assigned by the server

            // Start a thread to listen for messages from the server
            new Thread(() -> {
                try {
                    while (true) {
                        String message = player.receiveMessage();
                        if (message == null) {
                            break; // Server disconnected
                        }
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Read input from the user and send it to the server
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                player.sendMessage(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}