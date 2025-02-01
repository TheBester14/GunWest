package network;

import java.io.IOException;
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
                Player player = new Player(socket, nextPlayerId++);
                players.add(player);
                System.out.println("Player " + player.getPlayerId() + " connected!");

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
                System.out.println("Received from Player " + player.getPlayerId() + ": " + message);

                // Broadcast the message to all players
                broadcast(message, player.getPlayerId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                player.close();
                players.remove(player);
                System.out.println("Player " + player.getPlayerId() + " disconnected.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message, int senderId) {
        for (Player p : players) {
            if (p.getPlayerId() != senderId) {
                p.sendMessage("Player " + senderId + ": " + message);
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}