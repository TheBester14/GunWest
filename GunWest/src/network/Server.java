package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        System.out.println("Server started. Waiting for players...");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                // Prompt the player for their username
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Enter your username:");
                String username = in.readLine();

                Player player = new Player(socket, nextPlayerId++, username);
                players.add(player);
                System.out.println("Player " + player.getUsername() + " connected!");

                // Broadcast to all players that a new player has joined
                broadcast(player.getUsername() + " has joined the game!", -1);

                // Start a new thread to handle communication with this player
                new Thread(() -> handlePlayer(player)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handlePlayer(Player player) {
        try {
            while (true) {
                String message = player.receiveMessage();
                if (message == null) {
                    break; // Player disconnected
                }
                System.out.println("Received from " + player.getUsername() + ": " + message);

                // Broadcast the message to all players, including the sender's username
                broadcast(player.getUsername() + ": " + message, player.getPlayerId());
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private void broadcast(String message, int senderId) {
        for (Player p : players) {
            if (p.getPlayerId() != senderId) { // Don't send the message back to the sender
                p.sendMessage(message);
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}