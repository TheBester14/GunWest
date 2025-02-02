package network;

import java.util.HashMap;
import java.util.Map;

import entities.Player;
import main.KeyHandler;
import main.MouseHandler;
import tile.TileManager;

public class GameState {
    private Map<Integer, Player> players; // Key: playerId, Value: PlayerData

    public GameState() {
        players = new HashMap<>();
    }

    // Add a new player to the game state
    public void addPlayer(int playerId, KeyHandler keyHandler, MouseHandler mouseHandler, TileManager tileM, String name) {
        players.put(playerId, new Player(keyHandler, mouseHandler, tileM, name));
    }

    // Remove a player from the game state
    public void removePlayer(int playerId) {
        players.remove(playerId);
    }



    // Update a player's health
    public void updatePlayerHealth(int playerId, int health) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setHp(health);
        }
    }


    // Get the current game state as a string
    public String getGameState() {
        StringBuilder state = new StringBuilder();
        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            state.append("Player ").append(entry.getKey()).append(": ")
                 .append(player.getName()).append(", ")
                 .append("Position: (").append(player.getX()).append(", ").append(player.getY()).append("), ")
                 .append("Health: ").append(player.getHp()).append(", ")
                 .append()
        }
        return state.toString();
    }

    // Get all players in the game state
    public Map<Integer, Player> getPlayers() {
        return players;
    }
}