package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
                // Accept a new client connection.
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // The first message from the client is the username.
                String username = in.readLine();
                
                // Create a new Player object.
                Player player = new Player(socket, nextPlayerId++, username);
                players.add(player);
                System.out.println("Player " + player.getUsername() + " connected (ID=" + player.getPlayerId() + ").");
                
                // Announce that a new player joined.
                broadcast("CHAT Server: " + player.getUsername() + " joined!", -1);
                
                // Send a WELCOME message with the assigned id and initial position.
                player.sendMessage("WELCOME " + player.getPlayerId() + " " + player.getX() + " " + player.getY());
                
                // Start a new thread to handle this player's messages.
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

                // Handle MOVE commands.
                if (message.toUpperCase().startsWith("MOVE")) {
                    // Expected format: "MOVE dx dy"
                    String[] parts = message.split(" ");
                    if (parts.length == 3) {
                        int dx = Integer.parseInt(parts[1]);
                        int dy = Integer.parseInt(parts[2]);
                        // Update the player's position.
                        player.setX(player.getX() + dx);
                        player.setY(player.getY() + dy);
                        
                        // Broadcast the updated position to all players.
                        broadcast("UPDATE " + player.getPlayerId() + " " + player.getX() + " " + player.getY(), -1);
                    }
                }
                else if (message.toUpperCase().startsWith("CHAT")) {
                    String chatContent = message.substring(4).trim();
                    broadcast("CHAT " + player.getUsername() + ": " + chatContent, -1);
                }
                else {
                    // By default, treat any other message as chat.
                    broadcast("CHAT " + player.getUsername() + ": " + message, -1);
                }
            }
        } catch (IOException e) {
            // A disconnect or error occurred.
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
     * Broadcast a message to all players.
     * If senderId is not -1, you could choose to skip sending back to that client.
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
            server.start(); // Runs indefinitely.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
