package network;

import java.io.*;
import java.net.Socket;

public class Player {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public Player(Socket socket) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public String receiveMessage() throws IOException {
        return input.readLine();
    }

    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}