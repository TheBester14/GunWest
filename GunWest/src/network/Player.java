package network;

import java.io.*;
import java.net.Socket;

/**
 * Represents a server-side player. We add 'score' to track kills.
 */
public class Player {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    
    private int playerId;
    private String username;
    private int x, y;
    private double angle;
    
    private int hp = 240;
    private int score = 0; // new

    public Player(Socket socket, int playerId, String username) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);
        this.playerId = playerId;
        this.username = username;
        this.angle = 0.0;
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
    
    // getters / setters
    public int getPlayerId() { return playerId; }
    public String getUsername() { return username; }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }
    
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0,hp); }
    public void takeDamage(int dmg) { setHp(this.hp - dmg); }
    
    public int getScore() { return score; }
    public void setScore(int newScore) { this.score = newScore; }
}
