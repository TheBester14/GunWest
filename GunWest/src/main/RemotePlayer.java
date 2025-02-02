package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import tile.TileManager;

public class RemotePlayer {
    private int id;
    private int x,y;
    private double angle;
    private int width=50,height=50;
    private BufferedImage sprite;
    private ArrayList<RemoteBullet> bullets;

    private int hp=240;
    private int score=0;

    private TileManager tileM;

    public RemotePlayer(int x, int y, TileManager tileM) {
        this.x = x;
        this.y = y;
        this.angle=0.0;
        this.tileM= tileM;
        bullets=new ArrayList<>();
        // Load sprite if you want
    }

    public void setId(int i){id=i;}
    public int getId(){return id;}
    public void setPosition(int xx,int yy){x=xx; y=yy;}
    public void setAngle(double a){angle=a;}
    public double getAngle(){return angle;}

    public void fireBullet(int startX,int startY,double bulletAngle){
        bullets.add(new RemoteBullet(startX,startY,bulletAngle,id,tileM));
    }

    public void update(){
        // update bullets
        for(int i=bullets.size()-1;i>=0;i--){
            RemoteBullet rb=bullets.get(i);
            rb.update();
            if(rb.isDestroyed()){
                bullets.remove(i);
            }
        }
    }

    public void draw(Graphics g){
        // optionally draw your remote player's sprite at (x,y) with rotation=angle
        // ...
        // draw bullets
        for(RemoteBullet rb: bullets){
            rb.draw((Graphics2D)g);
        }
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height);
    }

    // HP
    public int getHp(){return hp;}
    public void setHp(int h){
        if(h<0)h=0;
        hp=h;
    }
    // Score
    public int getScore(){return score;}
    public void setScore(int s){score=s;}

    public ArrayList<RemoteBullet> getBullets(){return bullets;}
}
