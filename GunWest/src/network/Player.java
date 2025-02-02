package network;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Player {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    
    private int playerId;
    private String username;
    private int x, y;
    private double angle;  // New field for sprite rotation

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

	public Socket getSocket() { return socket; }
	
	public void sendAudio(byte[] audioData) {
	    try {
	        OutputStream out = socket.getOutputStream();
	        out.write(audioData);
	        out.flush();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public byte[] receiveAudio() {
	    try {
	        InputStream in = socket.getInputStream();
	        byte[] buffer = new byte[4096];
	        int bytesRead = in.read(buffer);
	        if (bytesRead > 0) {
	            return Arrays.copyOf(buffer, bytesRead);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
}

