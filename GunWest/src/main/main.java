package main;

import java.io.IOException;

import network.Client;
import network.Server;

public class main {

	public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("host")) {
            try {
                Server server = new Server(5000);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Client client = new Client();
            client.start();
        }
	}

}
