package network;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import main.GamePanel;

/**
 * A client that connects to the server on port 5000, 
 * and passes messages to/from the GamePanel.
 */
public class Client implements NetworkSender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    private int myId = -1;
    private GamePanel gamePanel;

    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
        // pass the client ref to the gamePanel, so gamePanel can do netClient.sendToServer
        gamePanel.setNetworkClient(this);
    }
    
    public void start() {
        // prompt for IP and username
        String host = JOptionPane.showInputDialog("Enter server IP:", "127.0.0.1");
        if (host == null || host.isEmpty()) {
            System.out.println("No IP. Exiting client.");
            return;
        }
        String username = JOptionPane.showInputDialog("Enter username:", "Player");
        if (username == null || username.isEmpty()) {
            System.out.println("No username. Exiting client.");
            return;
        }
        
        try {
            socket = new Socket(host, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // send username
            out.println(username);
            
            // start a thread to read from server
            new Thread(() -> listenToServer()).start();
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void listenToServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handleServerMessage(line);
            }
        } catch (IOException e) {
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
            gamePanel.setMyId(myId);
            System.out.println("WELCOME: My ID=" + myId + 
                               " starting pos=(" + startX + "," + startY + ")");
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
            if (id == myId) {
                gamePanel.player.setHp(newHp);
            } else {
                if (gamePanel.remotePlayers.containsKey(id)) {
                    gamePanel.remotePlayers.get(id).setHp(newHp);
                }
            }
        }
        else if (message.startsWith("CHAT")) {
            String chatLine = message.substring(4).trim();
            System.out.println(chatLine);
        } else if (message.startsWith("SCOREUPDATE")) {
            // e.g. "SCOREUPDATE <id> <score>"
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int newScore = Integer.parseInt(parts[2]);
            // if it's me
            if (id == myId) {
                gamePanel.player.setScore(newScore);
            } else {
                if (gamePanel.remotePlayers.containsKey(id)) {
                    gamePanel.remotePlayers.get(id).setScore(newScore);
                }
            }
        } 
        else if (message.startsWith("ROUNDRESET")) {
            // The server wants us to remove any bullets, 
            // or do any logic to reset UI, etc.
            // We'll call a function in GamePanel:
            gamePanel.resetRoundLocally();
        }
        else if (message.startsWith("GAMEOVER")) {
            // e.g. "GAMEOVER <winnerId>"
            String[] parts = message.split(" ");
            int winnerId = Integer.parseInt(parts[1]);
            // show a message, prevent more input, etc
            System.out.println("Server says: Game Over! Player " + winnerId + " wins!");
            gamePanel.setGameOver(true); // we can define that in gamePanel
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
