package network;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import main.GamePanel;

public class Client implements NetworkSender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    private int myId = -1;
    private GamePanel gamePanel;
    
    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
        // Pass the client reference to the GamePanel (so it can sendToServer).
        gamePanel.setNetworkClient(this);
    }
    
    public void start() {
        // Prompt for server IP and username
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
            out.println(username);
            new Thread(this::listenToServer).start();
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
        if (message.startsWith("WELCOME")) {
            // WELCOME <id> <x> <y>
            String[] parts = message.split(" ");
            myId = Integer.parseInt(parts[1]);
            int startX = Integer.parseInt(parts[2]);
            int startY = Integer.parseInt(parts[3]);
            gamePanel.player.setX(startX);
            gamePanel.player.setY(startY);
            // Pass myId to gamePanel so local collision code can filter my bullets.
            gamePanel.setMyId(myId);
            System.out.println("WELCOME: My ID=" + myId + " starting pos=(" + startX + "," + startY + ")");
        }
        else if (message.startsWith("UPDATE")) {
            // UPDATE <id> <x> <y>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int x  = Integer.parseInt(parts[2]);
            int y  = Integer.parseInt(parts[3]);
            if (id != myId) {
                gamePanel.updateRemotePlayer(id, x, y);
            }
        }
        else if (message.startsWith("ROTATE")) {
            // ROTATE <id> <angle>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            double angle = Double.parseDouble(parts[2]);
            if (id != myId) {
                gamePanel.updateRemotePlayerRotation(id, angle);
            }
        }
        else if (message.startsWith("BULLET")) {
            // BULLET <ownerId> <startX> <startY> <angle>
            String[] parts = message.split(" ");
            int ownerId = Integer.parseInt(parts[1]);
            int startX  = Integer.parseInt(parts[2]);
            int startY  = Integer.parseInt(parts[3]);
            double bulletAngle = Double.parseDouble(parts[4]);
            if (ownerId != myId) {
                gamePanel.remotePlayerBulletFired(ownerId, startX, startY, bulletAngle);
            }
        }
        else if (message.startsWith("HPUPDATE")) {
            // HPUPDATE <id> <hp>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int newHp = Integer.parseInt(parts[2]);
            // If it's me, update my local player's HP, else a remote player's HP.
            if (id == myId) {
                gamePanel.player.setHp(newHp);
            } else {
                // If we want to store remote HP for possible UI, do:
                if (gamePanel.remotePlayers.containsKey(id)) {
                    gamePanel.remotePlayers.get(id).setHp(newHp);
                }
            }
        }
        else if (message.startsWith("CHAT")) {
            String chatLine = message.substring(4).trim();
            System.out.println(chatLine);
        }
        else {
            System.out.println("Server> " + message);
        }
    }
    
    @Override
    public void sendToServer(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
