package network;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // My player ID and the map of all players (by ID)
    private int myId = -1;
    private Map<Integer, PlayerState> playersMap = new HashMap<>();

    // GUI: the game window
    private GameClientFrame gameFrame;

    public void start() {
        // Ask the user for server IP and username using dialogs.
        String host = JOptionPane.showInputDialog(null, "Enter server IP:", "Server IP", JOptionPane.QUESTION_MESSAGE);
        if (host == null || host.isEmpty()) {
            System.out.println("No server IP provided. Exiting.");
            return;
        }
        String username = JOptionPane.showInputDialog(null, "Enter username:", "Username", JOptionPane.QUESTION_MESSAGE);
        if (username == null || username.isEmpty()) {
            System.out.println("No username provided. Exiting.");
            return;
        }
        
        try {
            // Connect to the server on port 5000.
            socket = new Socket(host, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the username to the server.
            out.println(username);

            // Create the game GUI on the Swing thread.
            SwingUtilities.invokeLater(() -> {
                gameFrame = new GameClientFrame(playersMap);
                gameFrame.setVisible(true);
                gameFrame.setClientRef(this);
            });

            // Start a thread to listen for server messages.
            new Thread(() -> listenToServer()).start();

            // (Optional) You can uncomment this section if you wish to send chat messages
            // via the console.
            /*
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if (input.startsWith("/chat ")) {
                    out.println("CHAT " + input.substring(6));
                } else {
                    out.println(input);
                }
            }
            */
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Could not connect to server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listenToServer() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handleServerMessage(line);
                // Refresh the GUI after each update.
                if (gameFrame != null) {
                    SwingUtilities.invokeLater(() -> gameFrame.refresh());
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        }
    }

    private void handleServerMessage(String message) {
        if (message.startsWith("WELCOME")) {
            // Format: "WELCOME <id> <x> <y>"
            String[] parts = message.split(" ");
            myId = Integer.parseInt(parts[1]);
            int startX = Integer.parseInt(parts[2]);
            int startY = Integer.parseInt(parts[3]);

            // Add “Me” to the players map.
            PlayerState me = new PlayerState(myId, startX, startY, "Me");
            playersMap.put(myId, me);

            System.out.println("WELCOME: My ID=" + myId + " starting pos=(" + startX + "," + startY + ")");
        }
        else if (message.startsWith("UPDATE")) {
            // Format: "UPDATE <id> <x> <y>"
            String[] parts = message.split(" ");
            int pid = Integer.parseInt(parts[1]);
            int px  = Integer.parseInt(parts[2]);
            int py  = Integer.parseInt(parts[3]);

            // If the player isn’t yet in the map, add him.
            if (!playersMap.containsKey(pid)) {
                playersMap.put(pid, new PlayerState(pid, px, py, "P" + pid));
            } else {
                PlayerState ps = playersMap.get(pid);
                ps.x = px;
                ps.y = py;
            }
            if (pid == myId) {
                System.out.println("Server updated my position: (" + px + "," + py + ")");
            } else {
                System.out.println("Player " + pid + " moved to (" + px + "," + py + ")");
            }
        }
        else if (message.startsWith("CHAT")) {
            String chatLine = message.substring(4).trim();
            System.out.println(chatLine);
        }
        else {
            System.out.println("Server> " + message);
        }
    }

    /** 
     * Called by the GameClientFrame (via key presses) to send a command (such as "MOVE dx dy")
     * to the server.
     */
    public void sendToServer(String msg) {
        out.println(msg);
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
