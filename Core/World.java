package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.Serializable;
import java.util.Random;
import java.io.File;

public class World implements Serializable {
    private TETile[][] world;
    private TETile[][] darkWorld;
    private int playerXPos;
    private int playerYPos;
    private Random random;
    private long seed;

    public World(TETile[][] worldArray, Random r, long s) {
        world = worldArray;
        random = r;
        seed = s;
    }

    public void spawnPlayer(int xPos, int yPos) {
        if (xPos != -1 || yPos != -1) {
            playerXPos = xPos;
            playerYPos = yPos;
        } else {
            int x = 0;
            int y = 0;
            while (world[x][y] != WorldGeneration.floor()) {
                x = RandomUtils.uniform(random, Engine.WIDTH);
                y = RandomUtils.uniform(random, Engine.HEIGHT);
            }
            playerXPos = x;
            playerYPos = y;
        }
        world[playerXPos][playerYPos] = WorldGeneration.player();
    }

    public int movePlayer(char c, int newRange) {
        int newX = playerXPos;
        int newY = playerYPos;
        if (c == 'w' || c == 'W') {
            newY += 1;
        } else if (c == 's' || c == 'S') {
            newY -= 1;
        } else if (c == 'd' || c == 'D') {
            newX += 1;
        } else if (c == 'a' || c == 'A') {
            newX -= 1;
        }
        if (!(newX >= 0 && newX < Engine.WIDTH && newY >= 0 && newY < Engine.HEIGHT)) {
            return newRange;
        }
        if (!(world[newX][newY] == WorldGeneration.floor())
                && !(world[newX][newY] == WorldGeneration.flashlight())
                && !(world[newX][newY] == WorldGeneration.escapePod())) {
            return newRange;
        } else if (world[newX][newY] == WorldGeneration.flashlight()) {
            newRange = 6;
        } else if (world[newX][newY] == WorldGeneration.escapePod()) {
            newRange = -1;
        }
        world[playerXPos][playerYPos] = WorldGeneration.floor();
        world[newX][newY] = WorldGeneration.player();
        playerXPos = newX;
        playerYPos = newY;
        return newRange;
    }

    public int movePlayerString(char c, int newRange) {
        int newX = playerXPos;
        int newY = playerYPos;
        if (c == 'w' || c == 'W') {
            newY += 1;
        } else if (c == 's' || c == 'S') {
            newY -= 1;
        } else if (c == 'd' || c == 'D') {
            newX += 1;
        } else if (c == 'a' || c == 'A') {
            newX -= 1;
        }
        if (!(newX >= 0 && newX < Engine.WIDTH && newY >= 0 && newY < Engine.HEIGHT)) {
            return newRange;
        }
        if (!(world[newX][newY] == WorldGeneration.floor())
                && !(world[newX][newY] == WorldGeneration.flashlight())) {
            return newRange;
        } else if (world[newX][newY] == WorldGeneration.flashlight()) {
            newRange = 6;
        }
        world[playerXPos][playerYPos] = WorldGeneration.floor();
        world[newX][newY] = WorldGeneration.player();
        playerXPos = newX;
        playerYPos = newY;
        return newRange;
    }

    public void displayWorld() {
        TERenderer ter = new TERenderer();
        ter.initialize(82, 52);
        ter.renderFrame(world);
        StdDraw.show();
    }

    public void save(boolean lightsOn, int range, int stepsRemaining) {
        int lights;
        if (lightsOn) {
            lights = 1;
        } else {
            lights = 0;
        }
        File cwd = new File(System.getProperty("user.dir"));
        File saveFile = new File(cwd, "saveFile.txt");
        long[] save = new long[]{seed, playerXPos, playerYPos, lights, range, stepsRemaining};
        Utils.writeObject(saveFile, save);
    }

    public TETile[][] world() {
        return world;
    }

    public int playerXPos() {
        return playerXPos;
    }

    public int playerYPos() {
        return playerYPos;
    }

    public Random getRandom() {
        return random;
    }

    public long seed() {
        return seed;
    }
}
