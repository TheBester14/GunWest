package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import main.GamePanel;
import javax.imageio.ImageIO;

public class TileManager {
    
    public GamePanel gp;
    public Tile[] tile;
    public int mapTileNumber[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[50];
        mapTileNumber = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
        loadMap("/maps/mp_desert.txt");
    }
    
    public void getTileImage() {
        setup(0, "Desert1", false);
        setup(1, "Desert2", true);
        setup(2, "Desert3", true);
        setup(3, "Desert4", false);
        setup(4, "Desert5", false);
        

    }
    
    public void setup(int index, String imageName, boolean collision) {
        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(new File("ressources/tile/" + imageName + ".png"));
            tile[index].collision = collision;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            int col = 0;
            int row = 0;
            
            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                String[] numbers = line.split(" ");
                
                for (col = 0; col < gp.maxWorldCol; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNumber[col][row] = num;
                }
                row++;
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldRow < gp.maxWorldRow) {
            worldCol = 0;
            while (worldCol < gp.maxWorldCol) {
                int tileNum = mapTileNumber[worldCol][worldRow];

                // Calculer les coordonnées d'affichage de la tuile
                int screenX = worldCol * gp.tileSize;
                int screenY = worldRow * gp.tileSize;

                // Dessiner la tuile
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                worldCol++;
            }
            worldRow++;
        }
    }
}