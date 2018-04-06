package ru.deadsoftware.cavecraft.game;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.TimeUtils;

public class WorldGen {

    private static int[][] foreMap, backMap;
    private static int[] noise;

    static int[] genNoise(int width, int mid, int min, int max) {
        RandomXS128 rand = new RandomXS128(TimeUtils.millis());
        int[] res = new int[width];
        int t;
        res[0] = mid;
        for (int i=1; i<width; i++) {
            t = rand.nextInt(3)-1;
            res[i] = res[i-1] + t;
            if (res[i]<min) res[i] = min;
            if (res[i]>max) res[i] = max;
        }
        return res;
    }

    static void genWorld(int width, int height) {
        foreMap = new int[width][height];
        backMap = new int[width][height];
        noise = genNoise(width, height/2, 1, height);
        for (int x=0; x<width; x++) {
            for (int y=height-noise[x]; y<height; y++) {
                if (y==height-noise[x]) {
                    foreMap[x][y] = 3;
                    backMap[x][y] = 3;
                } else if (y<height-noise[x]+4) {
                    foreMap[x][y] = 2;
                    backMap[x][y] = 2;
                } else {
                    foreMap[x][y] = 1;
                    backMap[x][y] = 1;
                }
            }
        }
    }

    static int[][] getForeMap() {
        return foreMap;
    }

    static int[][] getBackMap() {
        return backMap;
    }

    static void clear() {
        foreMap = null;
        backMap = null;
    }
}
