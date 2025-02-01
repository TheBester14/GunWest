package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public void start() {
        Scanner scanner = new Scanner(System.in);

        // Prompt the user for the host's IP address
        System.out.print("Enter the host's IP address: ");
        String host = scanner.nextLine();

        // Prompt the user for their username
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        try {
            // Connect to the server using the provided IP address
            Socket socket = new Socket(host, 5000); // Port 5000 is used by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send the username to the server
            out.println(username);

            // Start a thread to listen for messages from the server
            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readLine();
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
            while (true) {
                String input = scanner.nextLine();
                out.println(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to connect to the server. Please check the IP address and try again.");
            e.printStackTrace();
        }
    }
}