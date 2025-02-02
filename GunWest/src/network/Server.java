package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal 1v1 server: 
 * - Player #0 spawns at (600,50), #1 at (600,600).
 * - On new join, we broadcast the new player’s info to everyone 
 *   and also send existing players' info to the newcomer.
 */
public class Server {
    private ServerSocket serverSocket;
    private List<Player> players; // each has an ID, X,Y, angle, HP, score
    private int nextPlayerId = 0; 
    private static final int MAX_SCORE = 3; // e.g. best of 5

    // If you want a "gameOver" flag, you can. This example is minimal.

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        players = new ArrayList<>();
    }

    public void start() {
        System.out.println("Server started. Listening on port " 
                           + serverSocket.getLocalPort());
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));
                
                String username = in.readLine(); // first msg from client is username
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

                System.out.println("Player " + username + " connected (ID=" 
                                   + player.getPlayerId() + ").");
                broadcast("CHAT Server: " + username + " joined!", -1);

                // 1) Send WELCOME for the new player
                player.sendMessage("WELCOME " + player.getPlayerId() 
                    + " " + player.getX() + " " + player.getY());
                
                // 2) Send existing players' info to the new guy
                for (Player p : players) {
                    if (p.getPlayerId() != player.getPlayerId()) {
                        player.sendMessage("UPDATE " + p.getPlayerId() + " " 
                            + p.getX() + " " + p.getY());
                        player.sendMessage("ROTATE " + p.getPlayerId() + " " 
                            + p.getAngle());
                        player.sendMessage("HPUPDATE " + p.getPlayerId() + " " 
                            + p.getHp());
                        player.sendMessage("SCOREUPDATE " + p.getPlayerId() + " " 
                            + p.getScore());
                    }
                }
                
                // 3) Broadcast the new player's info to existing players
                broadcast("UPDATE " + player.getPlayerId() + " " 
                          + player.getX() + " " + player.getY(), player.getPlayerId());
                broadcast("ROTATE " + player.getPlayerId() + " " 
                          + player.getAngle(), player.getPlayerId());
                broadcast("HPUPDATE " + player.getPlayerId() + " " 
                          + player.getHp(), player.getPlayerId());
                broadcast("SCOREUPDATE " + player.getPlayerId() + " " 
                          + player.getScore(), player.getPlayerId());
                
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
                System.out.println("From " + player.getUsername() + ": " + message);

                if (message.toUpperCase().startsWith("MOVE")) {
                    // "MOVE dx dy"
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        int dx = Integer.parseInt(parts[1]);
                        int dy = Integer.parseInt(parts[2]);
                        player.setX(player.getX() + dx);
                        player.setY(player.getY() + dy);
                        // broadcast "UPDATE" to all but the sender
                        broadcast("UPDATE " + player.getPlayerId() + " " 
                                  + player.getX() + " " + player.getY(), -1);
                    }
                } 
                else if (message.toUpperCase().startsWith("ROTATE")) {
                    // "ROTATE <angle>"
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
                        // broadcast to all including the sender => pass senderId = -1
                        broadcast("BULLET " + player.getPlayerId() + " " 
                            + startX + " " + startY + " " + bulletAngle, -1);
                    }
                }
                else if (message.toUpperCase().startsWith("CHAT")) {
                    String chatContent = message.substring(4).trim();
                    broadcast("CHAT " + player.getUsername() + ": " + chatContent, -1);
                }
                else {
                    // handle e.g. DAMAGE, etc. Or do default chat
                    broadcast("CHAT " + player.getUsername() + ": " + message, -1);
                }
            }
        } catch (IOException e) {
            // player disconnected
        } finally {
            try { player.close(); } catch (IOException e) { e.printStackTrace(); }
            players.remove(player);
            System.out.println(player.getUsername() + " disconnected.");
            broadcast("CHAT Server: " + player.getUsername() + " left the game.", -1);
        }
    }

    private void broadcast(String msg, int senderId) {
        for (Player p : players) {
            // If senderId = -1 => send to everyone including the sender
            // Otherwise, skip the sender
            if (senderId == -1 || p.getPlayerId() != senderId) {
                p.sendMessage(msg);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(5000);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
