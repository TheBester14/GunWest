package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 1v1 server. 
 * Player #0 => (600,50), Player #1 => (600,600).
 * If a player kills the other, they get +1 score. 
 * First to 3 points => "Player X WINS" => game ends.
 * If kill happens but no one at 3 yet => reset both players to initial positions & HP=240.
 */
public class Server {
    private ServerSocket serverSocket;
    private List<Player> players; 
    private int nextPlayerId = 0; 
    private boolean gameOver = false; // If true, ignore further commands
    private static final int MAX_SCORE = 3;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        players = new ArrayList<>();
    }

    public void start() {
        System.out.println("Server started. Listening on port " + serverSocket.getLocalPort());
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                String username = in.readLine(); 
                Player player = new Player(socket, nextPlayerId, username);
                
                // 1v1 spawn logic
                if (nextPlayerId % 2 == 0) {
                    player.setX(600);
                    player.setY(50);
                } else {
                    player.setX(600);
                    player.setY(600);
                }
                players.add(player);

                System.out.println("Player " + player.getUsername() + " connected (ID=" + player.getPlayerId() + ").");
                broadcast("CHAT Server: " + player.getUsername() + " joined!", -1);

                // Send WELCOME
                player.sendMessage("WELCOME " + player.getPlayerId() 
                                   + " " + player.getX() 
                                   + " " + player.getY());

                // Send existing players' info to new player
                for (Player p : players) {
                    if (p.getPlayerId() != player.getPlayerId()) {
                        player.sendMessage("UPDATE " + p.getPlayerId() + " " + p.getX() + " " + p.getY());
                        player.sendMessage("ROTATE " + p.getPlayerId() + " " + p.getAngle());
                        player.sendMessage("HPUPDATE " + p.getPlayerId() + " " + p.getHp());
                        player.sendMessage("SCOREUPDATE " + p.getPlayerId() + " " + p.getScore());
                    }
                }
                // Broadcast new player's info
                broadcast("UPDATE " + player.getPlayerId() + " " 
                          + player.getX() + " " + player.getY(), player.getPlayerId());
                broadcast("ROTATE " + player.getPlayerId() + " " + player.getAngle(), player.getPlayerId());
                broadcast("HPUPDATE " + player.getPlayerId() + " " + player.getHp(), player.getPlayerId());
                broadcast("SCOREUPDATE " + player.getPlayerId() + " " + player.getScore(), player.getPlayerId());

                nextPlayerId++;
                
                new Thread(() -> handlePlayer(player)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePlayer(Player player) {
        try {
            String message;
            while ((message = player.receiveMessage()) != null) {
                // If game is over, ignore commands except maybe chat
                if (gameOver) {
                    if (message.toUpperCase().startsWith("CHAT")) {
                        String chatContent = message.substring(4).trim();
                        broadcast("CHAT " + player.getUsername() + ": " + chatContent, -1);
                    }
                    // else ignore
                    continue;
                }
                
                System.out.println("From " + player.getUsername() + ": " + message);

                if (message.toUpperCase().startsWith("MOVE")) {
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        int dx = Integer.parseInt(parts[1]);
                        int dy = Integer.parseInt(parts[2]);
                        player.setX(player.getX() + dx);
                        player.setY(player.getY() + dy);
                        broadcast("UPDATE " + player.getPlayerId() + " " 
                                  + player.getX() + " " + player.getY(), -1);
                    }
                }
                else if (message.toUpperCase().startsWith("ROTATE")) {
                    String[] parts = message.split(" ");
                    if (parts.length == 2) {
                        double angle = Double.parseDouble(parts[1]);
                        player.setAngle(angle);
                        broadcast("ROTATE " + player.getPlayerId() + " " 
                                  + angle, -1);
                    }
                }
                else if (message.toUpperCase().startsWith("BULLET")) {
                    // "BULLET <startX> <startY> <angle>"
                    String[] parts = message.split(" ");
                    if (parts.length == 4) {
                        int startX = Integer.parseInt(parts[1]);
                        int startY = Integer.parseInt(parts[2]);
                        double bulletAngle = Double.parseDouble(parts[3]);
                        // BROADCAST TO *ALL* PLAYERS (use -1) so even the shooter gets the message
                        broadcast("BULLET " + player.getPlayerId() + " " + startX
                                  + " " + startY + " " + bulletAngle, -1);
                    }
                }
                else if (message.toUpperCase().startsWith("DAMAGE")) {
                    // "DAMAGE <targetId> <amount>"
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        int targetId = Integer.parseInt(parts[1]);
                        int dmg = Integer.parseInt(parts[2]);
                        // 'player' is attacker
                        int attackerId = player.getPlayerId();
                        handleDamage(attackerId, targetId, dmg);
                    }
                }
                else if (message.toUpperCase().startsWith("CHAT")) {
                    String chatContent = message.substring(4).trim();
                    broadcast("CHAT " + player.getUsername() + ": " + chatContent, -1);
                }
                else {
                    broadcast("CHAT " + player.getUsername() + ": " + message, -1);
                }
            }
        } catch (IOException e) {
            // disconnected
        } finally {
            try { player.close(); } catch (IOException e) { e.printStackTrace(); }
            players.remove(player);
            System.out.println(player.getUsername() + " disconnected.");
            broadcast("CHAT Server: " + player.getUsername() + " left the game.", -1);
        }
    }

    private void handleDamage(int attackerId, int targetId, int dmg) {
        Player attacker = null;
        Player target   = null;
        for (Player p : players) {
            if (p.getPlayerId() == attackerId) attacker = p;
            if (p.getPlayerId() == targetId)   target   = p;
        }
        if (attacker == null || target == null) return;

        target.takeDamage(dmg);
        broadcast("HPUPDATE " + target.getPlayerId() + " " + target.getHp(), -1);

        if (target.getHp() <= 0) {
            // Attacker gets +1 score
            attacker.setScore(attacker.getScore() + 1);
            broadcast("SCOREUPDATE " + attackerId + " " + attacker.getScore(), -1);
            
            if (attacker.getScore() >= MAX_SCORE) {
                // GAME OVER
                broadcast("CHAT Server: Player " + attackerId + " WINS!");
                broadcast("GAMEOVER " + attackerId, -1);
                gameOver = true;
            } else {
                // Round reset
                resetRound();
            }
        }
    }

    private void resetRound() {
        // For 1v1, we assume players[0], players[1] exist
        if (players.size() < 2) return; // not enough players yet
        Player p0 = null;
        Player p1 = null;
        for (Player p : players) {
            if (p.getPlayerId() %2 ==0) {
                p0 = p;
            } else {
                p1 = p;
            }
        }
        if (p0 != null) {
            p0.setHp(240);
            p0.setX(600);
            p0.setY(50);
            broadcast("HPUPDATE " + p0.getPlayerId() + " 240", -1);
            broadcast("UPDATE " + p0.getPlayerId() + " " + p0.getX() + " " + p0.getY(), -1);
        }
        if (p1 != null) {
            p1.setHp(240);
            p1.setX(600);
            p1.setY(600);
            broadcast("HPUPDATE " + p1.getPlayerId() + " 240", -1);
            broadcast("UPDATE " + p1.getPlayerId() + " " + p1.getX() + " " + p1.getY(), -1);
        }
        
        // Optionally broadcast "ROUNDRESET" so clients can reset bullets, etc.
        broadcast("ROUNDRESET", -1);
    }

    private void broadcast(String msg, int senderId) {
        for (Player p : players) {
            // send to everyone except the 'senderId' if senderId != -1
            if (senderId == -1 || p.getPlayerId() != senderId) {
                p.sendMessage(msg);
            }
        }
    }


    public void close() throws IOException {
        serverSocket.close();
    }
}
