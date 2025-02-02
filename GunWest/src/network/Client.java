package network;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.JOptionPane;
import main.GamePanel;
import javax.sound.sampled.*;

public class Client implements NetworkSender {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private TargetDataLine microphone;
    private SourceDataLine speakers;
    
    private int myId = -1;
    private GamePanel gamePanel;
    
    public void setGamePanel(GamePanel gp) {
        this.gamePanel = gp;
        // Pass the client reference to the GamePanel (so it can sendToServer).
        gamePanel.setNetworkClient(this);
    }
    
    public void start() {
        // Prompt for server IP and username
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
            socket = new Socket(host, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the username to the server.
            out.println(username);
            startAudio();
            // Start a thread to listen for messages from the server.
            new Thread(() -> listenToServer()).start();

            out.println(username);
            new Thread(this::listenToServer).start();

        } catch(IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }
    
    private void listenToServer() {
        try {
        	InputStream in2 = socket.getInputStream();
            byte[] buffer = new byte[32768];
            while (true) {
                int bytesRead = in2.read(buffer);

                // Check if the data starts with "AUDIO:"
                if (new String(buffer, 0, 6).equals("AUDIO:")) {
                    // Extract the audio data (skip the "AUDIO:" prefix)
                    byte[] audioData = Arrays.copyOfRange(buffer, 6, bytesRead);
                    speakers.write(audioData, 0, audioData.length);
                } else {
                    // Treat it as a text message
                    String message = in.readLine();
                    handleServerMessage(message);
                }
            }
        } catch(IOException e) {
            System.out.println("Disconnected from server.");
        }
    }
    
    private void handleServerMessage(String message) {
        if (message.startsWith("WELCOME")) {
            // WELCOME <id> <x> <y>
            String[] parts = message.split(" ");
            myId = Integer.parseInt(parts[1]);
            int startX = Integer.parseInt(parts[2]);
            int startY = Integer.parseInt(parts[3]);
            gamePanel.player.setX(startX);
            gamePanel.player.setY(startY);
            // Pass myId to gamePanel so local collision code can filter my bullets.
            gamePanel.setMyId(myId);
            System.out.println("WELCOME: My ID=" + myId + " starting pos=(" + startX + "," + startY + ")");
        }
        else if (message.startsWith("UPDATE")) {
            // UPDATE <id> <x> <y>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int x  = Integer.parseInt(parts[2]);
            int y  = Integer.parseInt(parts[3]);
            if (id != myId) {
                gamePanel.updateRemotePlayer(id, x, y);
            }
        }
        else if (message.startsWith("ROTATE")) {
            // ROTATE <id> <angle>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            double angle = Double.parseDouble(parts[2]);
            if (id != myId) {
                gamePanel.updateRemotePlayerRotation(id, angle);
            }
        }
        else if (message.startsWith("BULLET")) {
            // BULLET <ownerId> <startX> <startY> <angle>
            String[] parts = message.split(" ");
            int ownerId = Integer.parseInt(parts[1]);
            int startX  = Integer.parseInt(parts[2]);
            int startY  = Integer.parseInt(parts[3]);
            double bulletAngle = Double.parseDouble(parts[4]);
            if (ownerId != myId) {
                gamePanel.remotePlayerBulletFired(ownerId, startX, startY, bulletAngle);
            }
        }
        else if (message.startsWith("HPUPDATE")) {
            // HPUPDATE <id> <hp>
            String[] parts = message.split(" ");
            int id = Integer.parseInt(parts[1]);
            int newHp = Integer.parseInt(parts[2]);
            // If it's me, update my local player's HP, else a remote player's HP.
            if (id == myId) {
                gamePanel.player.setHp(newHp);
            } else {
                // If we want to store remote HP for possible UI, do:
                if (gamePanel.remotePlayers.containsKey(id)) {
                    gamePanel.remotePlayers.get(id).setHp(newHp);
                }
            }
        }
        else if (message.startsWith("CHAT")) {
            String chatLine = message.substring(4).trim();
            System.out.println(chatLine);
        }
        else  if(message.startsWith("AUDIO")) {
            byte[] audioData = message.substring(5).getBytes();
            speakers.write(audioData, 0, audioData.length);
        } else{
            System.out.println("Server> " + message);
        }
    }
    
    public void startAudio() {
        try {
            AudioFormat format = new AudioFormat(8000, 8, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            System.out.println("Mic initialized and started.");
            info = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
            speakers.start();
            System.out.println("Speakers initialized and started.");
            new Thread(() -> captureAudio()).start();
            new Thread(() -> playAudio()).start();
            //new Thread(() -> testLocalLoopback()).start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    private void captureAudio() {
    	 byte[] buffer = new byte[2048];
    	    while (true) {
    	        int bytesRead = microphone.read(buffer, 0, buffer.length);
    	        if (bytesRead > 0) {
    	            // Prefix the audio data with "AUDIO:"
    	            String header = "AUDIO:";
    	            byte[] headerBytes = header.getBytes();
    	            byte[] combined = new byte[headerBytes.length + bytesRead];
    	            System.arraycopy(headerBytes, 0, combined, 0, headerBytes.length);
    	            System.arraycopy(buffer, 0, combined, headerBytes.length, bytesRead);
    	            sendToServer(combined);
            }
        }
    }

    private void sendToServer(byte[] combined) {
    	if (out != null) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(combined); // Send the raw byte array
                outputStream.flush(); // Ensure the data is sent immediately
            } catch (IOException e) {
                System.err.println("Error sending data to server: " + e.getMessage());
            }
	}
    }

	private void playAudio() {
        byte[] buffer = new byte[2048];
        while (true) {
            byte[] audioData = receiveAudio();
            if (audioData != null) {
            	System.out.println("Received audio data: " + audioData.length + " bytes");
                speakers.write(audioData, 0, audioData.length);
            }
        }
    }
    
    private byte[] receiveAudio() {
        try {
            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[2048];
            int bytesRead = in.read(buffer);
            if (bytesRead > 0) {
                return Arrays.copyOf(buffer, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    private void testLocalLoopback() {
        byte[] buffer = new byte[2048];
        while (true) {
            int bytesRead = microphone.read(buffer, 0, buffer.length);
            if (bytesRead > 0) {
                speakers.write(buffer, 0, bytesRead);
            }
        }
    }
    
    
    @Override
    public void sendToServer(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
    
    
}
