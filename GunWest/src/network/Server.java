package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Server that listens on port 5000.
 * - Each client is assigned (id, username).
 * - Reaches 6 kills => broadcast GAMEOVER <winnerId> <winnerUsername>.
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
        System.out.println("Server started. Listening on port " 
                           + serverSocket.getLocalPort());
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // first line from client: username
                String username = in.readLine();
                
                Player player = new Player(socket, nextPlayerId, username);
                
                // Example spawn logic
                if (nextPlayerId % 2 == 0) {
                    player.setX(600);
                    player.setY(50);
                } else {
                    player.setX(600);
                    player.setY(600);
                }
                
                player.setSpawnX(player.getX());
                player.setSpawnY(player.getY());
                
                players.add(player);
                System.out.println("Player " + player.getUsername() 
                    + " connected (ID=" + player.getPlayerId() + ").");
                
                // Send new player the names of all existing players
                for (Player existing : players) {
                    if (existing != player) {
                        player.sendMessage("NAME " 
                                + existing.getPlayerId() + " "
                                + existing.getUsername());
                    }
                }

                // Broadcast the new player's name to everyone else
                broadcast("NAME " + player.getPlayerId() + " " + player.getUsername(), 
                          player.getPlayerId());

                // Broadcast that they joined
                broadcast("CHAT Server: " + player.getUsername() + " joined!", -1);

                // Send WELCOME with assigned ID and initial position
                player.sendMessage("WELCOME " 
                    + player.getPlayerId() + " " 
                    + player.getX() + " " 
                    + player.getY());
                
                nextPlayerId++;
                
                // handle this player's messages in a new thread
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
                    // MOVE dx dy
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        int dx = Integer.parseInt(parts[1]);
                        int dy = Integer.parseInt(parts[2]);
                        player.setX(player.getX() + dx);
                        player.setY(player.getY() + dy);
                        broadcast("UPDATE " + player.getPlayerId() 
                                  + " " + player.getX() 
                                  + " " + player.getY(), -1);
                    }

                } else if (message.toUpperCase().startsWith("ROTATE")) {
                    // ROTATE <angle>
                    String[] parts = message.split(" ");
                    if (parts.length == 2) {
                        double angle = Double.parseDouble(parts[1]);
                        player.setAngle(angle);
                        broadcast("ROTATE " + player.getPlayerId() 
                                  + " " + angle, 
                                  player.getPlayerId());
                    }

                } else if (message.toUpperCase().startsWith("BULLET")) {
                    // BULLET <startX> <startY> <angle>
                    String[] parts = message.split(" ");
                    if (parts.length == 4) {
                        int startX = Integer.parseInt(parts[1]);
                        int startY = Integer.parseInt(parts[2]);
                        double bulletAngle = Double.parseDouble(parts[3]);
                        broadcast("BULLET " + player.getPlayerId() 
                                  + " " + startX + " " + startY 
                                  + " " + bulletAngle, 
                                  player.getPlayerId());
                    }

                } else if (message.toUpperCase().startsWith("DAMAGE")) {
                    // DAMAGE <targetId> <amount> <killerId>
                    String[] parts = message.split(" ");
                    if (parts.length == 4) {
                        int targetId = Integer.parseInt(parts[1]);
                        int dmg      = Integer.parseInt(parts[2]);
                        int killerId = Integer.parseInt(parts[3]);
                        
                        for (Player p : players) {
                            if (p.getPlayerId() == targetId) {
                                // apply damage
                                p.takeDamage(dmg);
                                broadcast("HPUPDATE " + targetId + " " + p.getHp(), -1);

                                // check if target died
                                if (p.getHp() <= 0) {
                                    // find the killer
                                    for (Player potentialKiller : players) {
                                        if (potentialKiller.getPlayerId() == killerId) {
                                            potentialKiller.incrementKills();
                                            int newKills = potentialKiller.getKills();

                                            // broadcast updated kills
                                            broadcast("SCOREUPDATE " + killerId + " " + newKills, -1);

                                            // if killer reached 6 => game over
                                            if (newKills >= 6) {
                                                String winnerUsername = potentialKiller.getUsername();
                                                // broadcast a chat
                                                broadcast("CHAT " + winnerUsername + " WON THE GAME!", -1);
                                                // broadcast GAMEOVER <id> <username>
                                                broadcast("GAMEOVER " + killerId 
                                                          + " " + winnerUsername, -1);
                                            }
                                            break;
                                        }
                                    }

                                    // respawn the dead player
                                    p.setHp(240);
                                    p.setX(p.getSpawnX());
                                    p.setY(p.getSpawnY());
                                    broadcast("HPUPDATE " + p.getPlayerId() + " " + p.getHp(), -1);
                                    broadcast("UPDATE " + p.getPlayerId() 
                                              + " " + p.getX() 
                                              + " " + p.getY(), -1);
                                }
                                break;
                            }
                        }
                    }
                } else if (message.toUpperCase().startsWith("CHAT")) {
                    // Chat
                    String chatContent = message.substring(4).trim();
                    broadcast("CHAT " + player.getUsername() + ": " + chatContent, -1);

                } else {
                    // default => broadcast as chat
                    broadcast("CHAT " + player.getUsername() + ": " + message, -1);
                }
            }
        } catch (IOException e) {
            // player disconnected
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

    /**
     * broadcast a message to all players except 'senderId' if needed
     */
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
