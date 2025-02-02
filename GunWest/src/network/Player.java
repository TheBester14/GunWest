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
    private double angle;
    
    private int hp = 240;
    private int kills = 0;
    
    // Store original spawn
    private int spawnX, spawnY;

    public Player(Socket socket, int playerId, String username) throws IOException {
        this.socket = socket;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream(), true);

        this.playerId = playerId;
        this.username = username;
    }

    /*** ADD THESE MISSING GETTERS ***/
    public int getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }
    /*********************************/

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(hp, 0); }

    public void takeDamage(int amount) {
        setHp(this.hp - amount);
    }

    public int getKills() { return kills; }
    public void incrementKills() { kills++; }

    public int getSpawnX() { return spawnX; }
    public void setSpawnX(int spawnX) { this.spawnX = spawnX; }

    public int getSpawnY() { return spawnY; }
    public void setSpawnY(int spawnY) { this.spawnY = spawnY; }

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
