package network;

import java.io.*;
import java.net.Socket;

public class Player {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    
    private int playerId;
    private String username;
    private int x, y;

    public Player(Socket socket, int playerId, String username) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.playerId = playerId;
        this.username = username;
        
        // Set a common starting position for every player.
        this.x = 100;
        this.y = 100;
    }

    public void sendMessage(String message) {
        output.println(message);
    }
    
    public String receiveMessage() throws IOException {
        return input.readLine();
    }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public int getPlayerId() { return playerId; }
    public String getUsername() { return username; }
    
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}
