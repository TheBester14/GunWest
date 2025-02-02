package network;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;



public class Client {
	private AudioHandler audioHandler;
    private OutputStream audioOutputStream;
    private InputStream audioInputStream;
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the host's IP address: ");
        String host = scanner.nextLine();
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        try {
            Socket socket = new Socket(host, 5000);
            audioOutputStream = socket.getOutputStream();
            audioInputStream = socket.getInputStream();
            audioHandler = new AudioHandler(audioOutputStream, audioInputStream);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);
            
            new Thread(() -> {
                try {
                	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String message = in.readLine();
                        if (message == null) {
                            break;
                        }
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
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