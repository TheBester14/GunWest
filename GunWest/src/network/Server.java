package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import entities.Player;

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
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println("Enter your username:");
                String username = in.readLine();
                Player player = new Player(socket, nextPlayerId++, username);
                players.add(player);
                System.out.println("Player " + player.getName() + " connected!");
                broadcast(player.getName() + " has joined the game!", -1);
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
                    break;
                }
                System.out.println("Received from " + player.getUsername() + ": " + message);
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
            if (p.getPlayerId() != senderId) {
                p.sendMessage(message);
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}