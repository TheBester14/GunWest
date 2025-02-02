package network;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    private Map<Integer, Player> players; // Key: playerId, Value: PlayerData

    public GameState() {
        players = new HashMap<>();
    }

    // Add a new player to the game state
    public void addPlayer(int playerId, String name) {
        players.put(playerId, new Player(name));
    }

    // Remove a player from the game state
    public void removePlayer(int playerId) {
        players.remove(playerId);
    }

    // Update a player's position
    public void updatePlayerPosition(int playerId, int x, int y) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setPosition(x, y);
        }
    }

    // Update a player's health
    public void updatePlayerHealth(int playerId, int health) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setHealth(health);
        }
    }

    // Increment a player's kill count
    public void incrementPlayerKills(int playerId) {
        Player player = players.get(playerId);
        if (player != null) {
            player.incrementKills();
        }
    }

    // Get the current game state as a string
    public String getGameState() {
        StringBuilder state = new StringBuilder();
        for (Map.Entry<Integer, Player> entry : players.entrySet()) {
            Player player = entry.getValue();
            state.append("Player ").append(entry.getKey()).append(": ")
                 .append(player.getUsername()).append(", ")
                 .append("Position: (").append(player.getX()).append(", ").append(player.getY()).append("), ")
                 .append("Health: ").append(player.getHealth()).append(", ")
                 .append("Kills: ").append(player.getKills()).append("\n");
        }
        return state.toString();
    }

    // Get all players in the game state
    public Map<Integer, Player> getPlayers() {
        return players;
    }
}