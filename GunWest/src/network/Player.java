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
    private double angle;  // For sprite rotation
    
    // ----------------- ADD THESE FIELDS & METHODS -----------------
    private int hp = 240;  // Default HP—matches the local “entities.Player”

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void takeDamage(int amount) {
        setHp(this.hp - amount);
    }
    // --------------------------------------------------------------
    
    public Player(Socket socket, int playerId, String username) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.playerId = playerId;
        this.username = username;
        
        // Set a common starting position for every player.
        this.x = 100;
        this.y = 100;
        this.angle = 0.0;
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
    
    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }
    
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}
