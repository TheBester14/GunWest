package network;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import main.GamePanel;

public class Client implements NetworkSender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    // My player ID as assigned by the server.
    private int myId = -1;
    
    // Reference to the GamePanel (your game) to update player positions.
    private GamePanel gamePanel;
    
    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
        // Inform the game panel that networking is active.
        gamePanel.setNetworkClient(this);
    }
    
    public void start() {
        // Ask for server IP and username via dialog.
        String host = JOptionPane.showInputDialog(null, "Enter server IP:", "Server IP", JOptionPane.QUESTION_MESSAGE);
        if (host == null || host.isEmpty()) {
            System.out.println("No server IP provided. Exiting.");
            return;
        }
        String username = JOptionPane.showInputDialog(null, "Enter username:", "Username", JOptionPane.QUESTION_MESSAGE);
        if (username == null || username.isEmpty()) {
            System.out.println("No username provided. Exiting.");
            return;
        }
        
        try {
            socket = new Socket(host, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Send the username to the server.
            out.println(username);
            
            // Start a thread to listen for messages from the server.
            new Thread(() -> listenToServer()).start();
        } catch(IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }
    
    private void listenToServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handleServerMessage(line);
            }
        } catch(IOException e) {
            System.out.println("Disconnected from server.");
        }
    }
    
    private void handleServerMessage(String message) {
        if(message.startsWith("WELCOME")) {
            // Format: "WELCOME <id> <x> <y>"
            String[] parts = message.split(" ");
            myId = Integer.parseInt(parts[1]);
            int startX = Integer.parseInt(parts[2]);
            int startY = Integer.parseInt(parts[3]);
            // For the local player, set the initial position.
            gamePanel.player.setX(startX);
            gamePanel.player.setY(startY);
            System.out.println("WELCOME: My ID=" + myId + " starting pos=(" + startX + "," + startY + ")");
        }
        else if(message.startsWith("UPDATE")) {
            // Format: "UPDATE <id> <x> <y>"
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            if(id != myId) {
                gamePanel.updateRemotePlayer(id, x, y);
            }
        }
        else if(message.startsWith("ROTATE")) {
            // Format: "ROTATE <id> <angle>"
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            double angle = Double.parseDouble(parts[2]);
            if(id != myId) {
                gamePanel.updateRemotePlayerRotation(id, angle);
            }
        }
        else if(message.startsWith("BULLET")) {
            // Format: "BULLET <id> <startX> <startY> <angle>"
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int startX = Integer.parseInt(parts[2]);
            int startY = Integer.parseInt(parts[3]);
            double bulletAngle = Double.parseDouble(parts[4]);
            if(id != myId) {
                gamePanel.remotePlayerBulletFired(id, startX, startY, bulletAngle);
            }
        }
        else if(message.startsWith("CHAT")) {
            String chatLine = message.substring(4).trim();
            System.out.println(chatLine);
        }
        else {
            System.out.println("Server> " + message);
        }
    }
    
    @Override
    public void sendToServer(String msg) {
        // Only send if the connection is established.
        if (out != null) {
            out.println(msg);
        }
    }
}
