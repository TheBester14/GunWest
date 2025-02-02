package network;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import main.GamePanel;

/**
 * A client that connects to server on port 5000,
 * receives messages, passes them to GamePanel, etc.
 */
public class Client implements NetworkSender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    private int myId = -1;
    private GamePanel gamePanel;

    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
        gamePanel.setNetworkClient(this);
    }
    
    public void start() {
        String host = JOptionPane.showInputDialog("Server IP:", "127.0.0.1");
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

            // Listen to server in a new thread
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
        } catch(IOException e) {
            System.out.println("Disconnected from server.");
        }
    }

    private void handleServerMessage(String message) {
        if (message.startsWith("WELCOME")) {
            // "WELCOME <id> <x> <y>"
            String[] parts = message.split(" ");
            myId = Integer.parseInt(parts[1]);
            int startX = Integer.parseInt(parts[2]);
            int startY = Integer.parseInt(parts[3]);
            gamePanel.player.setX(startX);
            gamePanel.player.setY(startY);
            gamePanel.setMyId(myId);
            System.out.println("WELCOME: My ID=" + myId 
                + " spawn=("+startX+","+startY+")");
        }
        else if (message.startsWith("UPDATE")) {
            // "UPDATE <id> <x> <y>"
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int x  = Integer.parseInt(parts[2]);
            int y  = Integer.parseInt(parts[3]);
            if (id != myId) {
                gamePanel.updateRemotePlayer(id, x, y);
            }
        }
        else if (message.startsWith("ROTATE")) {
            // "ROTATE <id> <angle>"
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            double angle = Double.parseDouble(parts[2]);
            if (id != myId) {
                gamePanel.updateRemotePlayerRotation(id, angle);
            }
        }
        else if (message.startsWith("BULLET")) {
            // "BULLET <ownerId> <startX> <startY> <angle>"
            String[] parts = message.split(" ");
            int ownerId = Integer.parseInt(parts[1]);
            int startX  = Integer.parseInt(parts[2]);
            int startY  = Integer.parseInt(parts[3]);
            double bulletAngle = Double.parseDouble(parts[4]);
            // always show bullet, even if it's ours
            gamePanel.remotePlayerBulletFired(ownerId, startX, startY, bulletAngle);
        }
        else if (message.startsWith("HPUPDATE")) {
            // "HPUPDATE <id> <hp>"
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
        else if (message.startsWith("SCOREUPDATE")) {
            // "SCOREUPDATE <id> <score>"
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int newScore = Integer.parseInt(parts[2]);
            if (id == myId) {
                gamePanel.player.setScore(newScore);
            } else {
                if (gamePanel.remotePlayers.containsKey(id)) {
                    gamePanel.remotePlayers.get(id).setScore(newScore);
                }
            }
        }
        else if (message.startsWith("ROUNDRESET")) {
            gamePanel.resetRoundLocally();
        }
        else if (message.startsWith("GAMEOVER")) {
            // "GAMEOVER <winnerId>"
            String[] parts = message.split(" ");
            int winnerId = Integer.parseInt(parts[1]);
            System.out.println("Server: Player " + winnerId + " wins!");
            gamePanel.setGameOver(true);
        }
        else if (message.startsWith("CHAT")) {
            System.out.println( message.substring(4).trim() );
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
