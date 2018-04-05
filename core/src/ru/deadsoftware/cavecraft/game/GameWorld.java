package ru.deadsoftware.cavecraft.game;

import com.badlogic.gdx.Gdx;

public class GameWorld {

    private final int WIDTH, HEIGHT;
    private int[][] foreMap;
    private int[][] backMap;

    public GameWorld(int w, int h) {
        WIDTH = w;
        HEIGHT = h;
        WorldGen.genWorld(WIDTH,HEIGHT);
        foreMap = WorldGen.getForeMap();
        backMap = WorldGen.getBackMap();
        WorldGen.clear();
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getForeMap(int x, int y) {
        int ret = 0;
        try {
            ret = foreMap[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            Gdx.app.error("GameWorld",e.toString());
        }
        return ret;
    }

    public void setForeMap(int x, int y, int value) {
        try {
            foreMap[x][y] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            Gdx.app.error("GameWorld", e.toString());
        }
    }

    public int getBackMap(int x, int y) {
        int ret = 0;
        try {
            ret = backMap[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            Gdx.app.error("GameWorld",e.toString());
        }
        return ret;
    }

    public void setBackMap(int x, int y, int value) {
        try {
            backMap[x][y] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            Gdx.app.error("GameWorld", e.toString());
        }
    }

    public void placeToForeground(int x, int y, int value) {
        if (getForeMap(x,y) == 0 || value == 0) setForeMap(x,y,value);
    }

    public void placeToBackground(int x, int y, int value) {
        if (getBackMap(x,y) == 0 || value == 0) setBackMap(x,y,value);
    }

}
