package network;

/**
 * A simple data class to hold a player’s id, x,y position and name.
 */
public class PlayerState {
    public int id;
    public int x;
    public int y;
    public String name;

    public PlayerState(int id, int x, int y, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.name = name;
    }
}
