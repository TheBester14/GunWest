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
    
    private String myUsername;  // So we can store & pass to local player

    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
        // Pass the client reference so the GamePanel can call sendToServer(...)
        gamePanel.setNetworkClient(this);
    }
    
    public String getUsername() {
        return myUsername;
    }

    public void start() {
        // Prompt for server IP and username
        String host = JOptionPane.showInputDialog(null, "Enter server IP:", 
            "Server IP", JOptionPane.QUESTION_MESSAGE);
        if (host == null || host.isEmpty()) {
            System.out.println("No server IP provided. Exiting.");
            return;
        }
        myUsername = JOptionPane.showInputDialog(null, "Enter username:", 
            "Username", JOptionPane.QUESTION_MESSAGE);
        if (myUsername == null || myUsername.isEmpty()) {
            System.out.println("No username provided. Exiting.");
            return;
        }
        
        try {
            socket = new Socket(host, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // First thing: send the username to server
            out.println(myUsername);

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
            gamePanel.setMyId(myId);
            
            // Also set our local player's name to the username we typed
            gamePanel.player.setName(myUsername);

        } else if (message.startsWith("NAME")) {
            // NAME <id> <username>
            // So we can store each player's name in either local or remote
            String[] parts = message.split(" ", 3);
            int id = Integer.parseInt(parts[1]);
            String name = parts[2];

            if (id == myId) {
                // It's me, set local player's name
                gamePanel.player.setName(name);
            } else {
                // It's a remote player
                // If we haven't created them yet in "UPDATE", we could do so or
                // store just the name. But typically "UPDATE" or "REMOTE" creation
                // happens first. We'll store name in the existing remote if possible:
                if (gamePanel.remotePlayers.containsKey(id)) {
                    gamePanel.remotePlayers.get(id).setName(name);
                } else {
                    // If the remote player doesn't exist yet,
                    // create a placeholder at (0,0) or so
                    // The real position will come from an "UPDATE" or "WELCOME"
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
                // Force local position = server position if you want
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
                // Optionally force local rotation
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
                // If you want to update UI scoreboard
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
            String chatLine = message.substring(4).trim();
            System.out.println(chatLine); 
        } else if (message.startsWith("GAMEOVER")) {
            // GAMEOVER <winnerId>
            String[] parts = message.split(" ");
            int winnerId = Integer.parseInt(parts[1]);

            // Let the GamePanel know the game is done
            gamePanel.setGameOver(true, winnerId);
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
