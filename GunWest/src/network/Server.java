package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
        System.out.println("Server started. Waiting for players...");
       
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                // Prompt the player for their username
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Enter your username:");
                String username = in.readLine();

                // Start a new thread to handle communication with this player
                
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
    		 InputStream in = null;
			try {
				in = player.getSocket().getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	        byte[] buffer = new byte[4096];
            while (true) {
            	 int bytesRead = 0;
				try {
					bytesRead = in.read(buffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 if (bytesRead == -1) {
                     break; // Player disconnected
                 }
                byte[] audioData = player.receiveAudio();
                if (audioData != null) {
                    handleAudio(player, audioData);
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
       
                if (message == null) {
                    break; // Player disconnected
                }
                if (new String(buffer, 0, 6).equals("AUDIO:")) {
                    // Extract the audio data (skip the "AUDIO:" prefix)
                    byte[] audioData1 = Arrays.copyOfRange(buffer, 6, bytesRead);
                    handleAudio(player, audioData1);
                } else {
                    // Treat it as a text message
                    String message1 = new String(buffer, 0, bytesRead);
                    System.out.println("Received from " + player.getUsername() + ": " + message1);
                    broadcast(player.getUsername() + ": " + message1, player.getPlayerId());
            }
           }
        } catch (IOException e) {

            e.printStackTrace();
            // Client disconnected

        } finally {
            try {
                player.close();
                players.remove(player);
                System.out.println(player.getUsername() + " disconnected.");
                broadcast(player.getUsername() + " has left the game.", -1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
            }
    	 } finally {
    		 System.out.println("Reached final");
    	 }
    }
    private void broadcast(String message, int senderId) {
        for (Player p : players) {
            if (p.getPlayerId() != senderId) { // Don't send the message back to the sender
                p.sendMessage(message);
            }
        }
    }
    
    private void handleAudio(Player player, byte[] audioData) {
        System.out.println("Broadcasting audio data: " + audioData.length + " bytes");
    	for (Player p : players) {
            if (p.getPlayerId() != player.getPlayerId()) {
                p.sendAudio(audioData);
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

