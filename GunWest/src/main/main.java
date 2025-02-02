package main;

import java.io.IOException;
import network.Client;
import network.Server;

public class main {
    public static void main(String[] args) {
        // If running as host, start the server.
        if (args.length > 0 && args[0].equals("host")) {
            new Thread(() -> {
                try {
                    Server server = new Server(5000);
                    server.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            
            // (Optional) Wait a moment for the server to initialize.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        
        // Create your game frame (which creates the GamePanel)
        GameFrame frame = new GameFrame();
        
        // Create and start the network client.
        Client client = new Client();
        client.setGamePanel(frame.getGamePanel());
        client.start();
    }
}
