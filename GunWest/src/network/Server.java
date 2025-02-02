package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple server that listens on port 5000.
 * Each connected client (Player) is assigned an ID, position, HP.
 * On receiving "DAMAGE <id> <amount>" the server updates that player's HP
 * and broadcasts "HPUPDATE <id> <newHp>" so all clients see the change.
 */
public class Server {
    private ServerSocket serverSocket;
    private List<Player> players; 
    private int nextPlayerId = 0; 

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
                // The first message from a client is the username.
                String username = in.readLine();
                
                // Create a new server-side Player for this connection.
                Player player = new Player(socket, nextPlayerId++, username);
                players.add(player);
                System.out.println("Player " + player.getUsername() + " connected (ID=" + player.getPlayerId() + ").");
                
                broadcast("CHAT Server: " + player.getUsername() + " joined!", -1);
                // Send WELCOME message with assigned ID and initial position.
                player.sendMessage("WELCOME " + player.getPlayerId() + " " + player.getX() + " " + player.getY());
                
                // Send existing players info to the new player (position, rotation, HP).
                for (Player p : players) {
                    if (p.getPlayerId() != player.getPlayerId()) {
                        player.sendMessage("UPDATE " + p.getPlayerId() + " " + p.getX() + " " + p.getY());
                        player.sendMessage("ROTATE " + p.getPlayerId() + " " + p.getAngle());
                        player.sendMessage("HPUPDATE " + p.getPlayerId() + " " + p.getHp());
                    }
                }
                // Broadcast new player's initial info to others.
                broadcast("UPDATE " + player.getPlayerId() + " " + player.getX() + " " + player.getY(), player.getPlayerId());
                broadcast("ROTATE " + player.getPlayerId() + " " + player.getAngle(), player.getPlayerId());
                broadcast("HPUPDATE " + player.getPlayerId() + " " + player.getHp(), player.getPlayerId());
                
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
                    // Format: MOVE dx dy
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        int dx = Integer.parseInt(parts[1]);
                        int dy = Integer.parseInt(parts[2]);
                        player.setX(player.getX() + dx);
                        player.setY(player.getY() + dy);
                        broadcast("UPDATE " + player.getPlayerId() + " " + player.getX() + " " + player.getY(), -1);
                    }
                }
                else if (message.toUpperCase().startsWith("ROTATE")) {
                    // ROTATE <angle>
                    String[] parts = message.split(" ");
                    if (parts.length == 2) {
                        double angle = Double.parseDouble(parts[1]);
                        player.setAngle(angle);
                        broadcast("ROTATE " + player.getPlayerId() + " " + angle, player.getPlayerId());
                    }
                }
                else if (message.toUpperCase().startsWith("BULLET")) {
                    // BULLET <startX> <startY> <angle>
                    String[] parts = message.split(" ");
                    if (parts.length == 4) {
                        int startX = Integer.parseInt(parts[1]);
                        int startY = Integer.parseInt(parts[2]);
                        double bulletAngle = Double.parseDouble(parts[3]);
                        // Broadcast so all see the bullet.
                        broadcast("BULLET " + player.getPlayerId() + " " + startX + " " + startY + " " + bulletAngle, player.getPlayerId());
                    }
                }
                else if (message.toUpperCase().startsWith("DAMAGE")) {
                    // DAMAGE <targetId> <amount>
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        int targetId = Integer.parseInt(parts[1]);
                        int dmg      = Integer.parseInt(parts[2]);
                        for (Player p : players) {
                            if (p.getPlayerId() == targetId) {
                                p.takeDamage(dmg);
                                // Broadcast updated HP.
                                broadcast("HPUPDATE " + targetId + " " + p.getHp(), -1);
                                break;
                            }
                        }
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
            // Client disconnected
        } finally {
            try {
                player.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            players.remove(player);
            System.out.println(player.getUsername() + " disconnected.");
            broadcast("CHAT Server: " + player.getUsername() + " left the game.", -1);
        }
    }

    private void broadcast(String message, int senderId) {
        for (Player p : players) {
            if (p.getPlayerId() != senderId) {
                p.sendMessage(message);
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
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
