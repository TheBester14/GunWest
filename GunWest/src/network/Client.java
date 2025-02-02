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

    private String myUsername;  // store local player's chosen username

    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
        // pass the client reference so the GamePanel can call sendToServer(...)
        gamePanel.setNetworkClient(this);
    }

    public String getUsername() {
        return myUsername;
    }

    /**
     * Prompt the user for server IP and username, then connect.
     */
    public void start() {
        // Prompt for server IP
        String host = JOptionPane.showInputDialog(
            null, "Enter server IP:", "Server IP",
            JOptionPane.QUESTION_MESSAGE
        );
        if (host == null || host.isEmpty()) {
            System.out.println("No server IP provided. Exiting.");
            return;
        }

        // Prompt for username
        myUsername = JOptionPane.showInputDialog(
            null, "Enter username:", "Username",
            JOptionPane.QUESTION_MESSAGE
        );
        if (myUsername == null || myUsername.isEmpty()) {
            System.out.println("No username provided. Exiting.");
            return;
        }

        try {
            // Connect to server
            socket = new Socket(host, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // First, send the username to server
            out.println(myUsername);

            // Start a thread to read messages from the server
            new Thread(this::listenToServer).start();

        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    /**
     * Continuously read lines from the server until disconnected.
     */
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

    /**
     * Handle all server messages that come in.
     */
    private void handleServerMessage(String message) {
        if (message.startsWith("WELCOME")) {
            // WELCOME <id> <x> <y>
            String[] parts = message.split(" ");
            myId = Integer.parseInt(parts[1]);
            int startX = Integer.parseInt(parts[2]);
            int startY = Integer.parseInt(parts[3]);

            // Set local player position and ID
            gamePanel.player.setX(startX);
            gamePanel.player.setY(startY);
            gamePanel.setMyId(myId);

            // Also set our local player's name to the username we typed
            gamePanel.player.setName(myUsername);

        } else if (message.startsWith("NAME")) {
            // NAME <id> <username>
            String[] parts = message.split(" ", 3);
            int id = Integer.parseInt(parts[1]);
            String name = parts[2];

            if (id == myId) {
                // It's me, set local player's name
                gamePanel.player.setName(name);
            } else {
                // It's a remote player
                if (gamePanel.remotePlayers.containsKey(id)) {
                    gamePanel.remotePlayers.get(id).setName(name);
                } else {
                    // If not created yet, create a placeholder
                    gamePanel.updateRemotePlayer(id, 600, 600);
                    gamePanel.remotePlayers.get(id).setName(name);
                }
            }

        } else if (message.startsWith("UPDATE")) {
            // UPDATE <id> <x> <y>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int x  = Integer.parseInt(parts[2]);
            int y  = Integer.parseInt(parts[3]);

            if (id == myId) {
                gamePanel.player.setX(x);
                gamePanel.player.setY(y);
            } else {
                gamePanel.updateRemotePlayer(id, x, y);
            }

        } else if (message.startsWith("ROTATE")) {
            // ROTATE <id> <angle>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            double angle = Double.parseDouble(parts[2]);
            if (id == myId) {
                gamePanel.player.setAngle(angle);
            } else {
                gamePanel.updateRemotePlayerRotation(id, angle);
            }

        } else if (message.startsWith("BULLET")) {
            // BULLET <ownerId> <startX> <startY> <angle>
            String[] parts = message.split(" ");
            int ownerId = Integer.parseInt(parts[1]);
            int startX  = Integer.parseInt(parts[2]);
            int startY  = Integer.parseInt(parts[3]);
            double bulletAngle = Double.parseDouble(parts[4]);
            if (ownerId != myId) {
                gamePanel.remotePlayerBulletFired(ownerId, startX, startY, bulletAngle);
            }

        } else if (message.startsWith("HPUPDATE")) {
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

        } else if (message.startsWith("SCOREUPDATE")) {
            // SCOREUPDATE <id> <newKills>
            String[] parts = message.split(" ");
            int sid      = Integer.parseInt(parts[1]);
            int newKills = Integer.parseInt(parts[2]);
            
            if (sid == myId) {
                // local
                gamePanel.player.setKills(newKills);
                gamePanel.ui.setScore(0, newKills);
                System.out.println("You scored a kill! (Kills=" + newKills + ")");
            } else {
                // remote
                if (gamePanel.remotePlayers.containsKey(sid)) {
                    gamePanel.remotePlayers.get(sid).setKills(newKills);
                    gamePanel.ui.setScore(1, newKills);
                    System.out.println("Remote player " + sid + " kills=" + newKills);
                }
            }

        } else if (message.startsWith("CHAT")) {
            // Chat
            String chatLine = message.substring(4).trim();
            System.out.println(chatLine);

        } else if (message.startsWith("GAMEOVER")) {
            // GAMEOVER <winnerId> <winnerUsername>
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) {
                // if the server didn't provide a username, fallback
                System.err.println("GAMEOVER message missing username! " + message);
                return;
            }
            int winnerId = Integer.parseInt(parts[1]);
            String winnerUsername = parts[2];

            // Now call the 3-parameter method in GamePanel
            gamePanel.setGameOver(true, winnerId, winnerUsername);

        } else {
            System.out.println("Server> " + message);
        }
    }

    /**
     * Expose a method for sending any message to the server
     */
    @Override
    public void sendToServer(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
