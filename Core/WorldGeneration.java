package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class WorldGeneration {

    private Random random;
    private int width;
    private int height;
    private HashMap<Integer, int[]> gridMap;
    private static TETile floor = Tileset.GRAY;
    private static TETile wall = Tileset.DARKGRAY;
    private static TETile nothing = Tileset.FLOOR;
    private static TETile player = Tileset.AVATAR;
    private static TETile flashlight = Tileset.FLASHLIGHT;
    private static TETile escapePod = Tileset.ESCAPEPOD;

    /**
    public static void main(String[] args) {
        random = new Random(167);
        width = 62;
        height = 62;
        gridMap = new HashMap<>();

        TERenderer ter = new TERenderer();
        ter.initialize(width, height);

        TETile[][] world = new TETile[width][height];
        setBlank(world);
        createRooms(world);
        connectRooms(world);
        connectRandom(world);
        //removeEnclosedWalls(world);
        ter.renderFrame(world);
    }
     */

    /** Generates a random world given a seed, width, and height */
    public TETile[][] initWorld(long seed, int w, int h, boolean fl) {
        random = new Random(seed);
        width = w;
        height = h;
        gridMap = new HashMap<>();

        TETile[][] world = new TETile[width][height];
        setBlank(world);
        createRooms(world);
        connectRooms(world);
        connectRandom(world);
        spawnEscapePod(world);
        if (fl) {
            spawnFlashlight(world);
        }
        return world;
    }

    /** Sets entire world as blank */
    public void setBlank(TETile[][] world) {
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = nothing;
            }
        }
    }

    /** Creates rooms randomly throughout world */
    public void createRooms(TETile[][] world) {
        int gridNumber = 1;
        for (int y = 1; y < height - 1; y += 10) {
            for (int x = 1; x < width - 1; x += 10) {
                drawRoom(world, x, y, gridNumber);
                gridNumber++;
            }
        }
    }

    /** Helper function for createRooms, draws rectangle or hexagonal room at given coordinates */
    public void drawRoom(TETile[][] world, int x, int y, int gridNumber) {
        int i = RandomUtils.uniform(random, 0, 100);
        String shape;
        if (i <= 40) {
            return;
        } else if (i <= 60) {
            shape = "Hexagon";
        } else {
            shape = "Rectangle";
        }
        int[] dimensions = getRoomDimensions(shape);
        int xPos = RandomUtils.uniform(random, x, x + (10 - dimensions[0] + 1));
        int yPos = RandomUtils.uniform(random, y + dimensions[1], y + 11);
        gridMap.put(gridNumber, new int[]{dimensions[0] / 2 + xPos, yPos - dimensions[1] / 2});
        if (shape.equals("Rectangle")) {
            drawRectangle(world, dimensions[0], dimensions[1], xPos, yPos);
        } else {
            drawHexagon(world, dimensions[1] / 2, xPos, yPos);
        }
    }

    /** Helper function for drawRoom, draws rectangular room at given coordinates */
    public void drawRectangle(TETile[][] tiles, int w, int h, int x, int y) {
        for (int i = h; i > 0; i--) {
            for (int j = 0; j < w; j++) {
                if (i == 1 || i == h || j == 0 || j == w - 1) {
                    tiles[x + j][y - i] = wall;
                } else {
                    tiles[x + j][y - i] = floor;
                }
            }
        }
    }

    /** Helper function for drawRoom, draws hexagonal room at given coordinates */
    public void drawHexagon(TETile[][] world, int sideLength, int xPos, int yPos) {
        xPos += sideLength - 1;
        int row = 1;
        int rowSize = sideLength;
        boolean bottom = false;
        while (row <= sideLength * 2) {
            for (int i = xPos; i < xPos + rowSize; i++) {
                if ((i == xPos || i == xPos + rowSize - 1)
                        || (row == 1 || row == sideLength * 2)) {
                    world[i][yPos] = wall;
                } else {
                    world[i][yPos] = floor;
                }

            }
            if (row < sideLength) {
                rowSize += 2;
                xPos -= 1;
            } else if ((row == sideLength) && !bottom) {
                bottom = true;
            } else {
                rowSize -= 2;
                xPos += 1;
            }
            row += 1;
            yPos -= 1;
        }
    }

    /** Helper function for drawRoom, generates random room size */
    public int[] getRoomDimensions(String shape) {
        if (shape.equals("Rectangle")) {
            int w = RandomUtils.uniform(random, 5, 11);
            int h = RandomUtils.uniform(random, 5, 11);
            return new int[]{w, h};
        } else {
            int sideLength = RandomUtils.uniform(random, 3, 5);
            return new int[]{sideLength + 2 * (sideLength - 1), sideLength * 2};
        }
    }

    /** Connects all rooms in the world with hallways */
    public void connectRooms(TETile[][] world) {
        int gridNumber = (width / 10) * (height / 10);
        QuickFindUF gridNumbers = new QuickFindUF(gridNumber + 1);
        for (int i = 0; i <= gridNumber; i++) {
            if (gridMap.get(i) == null) {
                gridNumbers.id()[i] = -1;
            }
        }
        for (int i = 1; i <= gridNumber; i++) {
            if (gridMap.get(i) == null) {
                continue;
            }
            int closest = findClosestRoom(i);
            drawHallway(world, gridMap.get(i), gridMap.get(closest));
            gridNumbers.union(i, closest);
        }

        while (!gridNumbers.oneSet()) {
            Set<Integer> set1 = new HashSet<>();
            Set<Integer> set2 = new HashSet<>();
            int parent = -1;
            for (int i = 1; parent == -1; i++) {
                parent = gridNumbers.id()[i];
            }
            for (int i = 1; i <= gridNumber; i++) {
                if (gridNumbers.find(i) == parent) {
                    set1.add(i);
                }
            }
            for (int i = 1; i <= gridNumber; i++) {
                if (gridNumbers.find(i) != -1 && gridNumbers.find(i) != parent) {
                    set2.add(i);
                }
            }
            int[] closest = findClosestRoomSet(set1, set2);
            if (gridMap.get(closest[1]) == null) {
                gridNumbers.union(closest[0], closest[1]);
                continue;
            }
            drawHallway(world, gridMap.get(closest[0]), gridMap.get(closest[1]));
            gridNumbers.union(closest[0], closest[1]);
        }
    }

    /** Draws hallway between given coordinates of 2 rooms */
    public void drawHallway(TETile[][] world, int[] room1, int[] room2) {
        int r = RandomUtils.uniform(random, 0, 2);
        if (r == 0) {
            drawURHallway(world, room1, room2);
        } else {
            drawRUHallway(world, room1, room2);
        }
    }

    /** Connects 6 pairs of rooms randomly */
    public void connectRandom(TETile[][] world) {
        for (int i = 0; i < 8; i++) {
            int room1 = 0;
            int room2 = 0;
            while (gridMap.get(room1) == null) {
                room1 = RandomUtils.uniform(random, 1, (width / 10) * (height / 10));
            }
            while (gridMap.get(room2) == null) {
                room2 = RandomUtils.uniform(random, 1, (width / 10) * (height / 10));
            }
            drawHallway(world, gridMap.get(room1), gridMap.get(room2));
        }
    }

    /** Helper function for drawHallway, hallway goes up then right */
    public void drawURHallway(TETile[][] world, int[] room1, int[] room2) {
        int x1 =  room1[0];
        int y1 = room1[1];
        int x2 = room2[0];
        int y2 = room2[1];

        int increment;
        if (x1 <= x2) {
            increment = 1;
        } else {
            increment = -1;
        }
        while (x1 != x2) {
            x1 += increment;
            world[x1][y1] = floor;
            if (world[x1][y1 + 1] == nothing) {
                world[x1][y1 + 1] = wall;
            }
            if (world[x1][y1 - 1] == nothing) {
                world[x1][y1 - 1] = wall;
            }
        }
        if (world[x1 + increment][y1] == nothing) {
            world[x1 + increment][y1] = wall;
        }
        if (y1 <= y2) {
            increment = 1;
        } else {
            increment = -1;
        }
        while (y1 != y2) {
            y1 += increment;
            world[x1][y1] = floor;
            if (world[x1 + 1][y1] == nothing) {
                world[x1 + 1][y1] = wall;
            }
            if (world[x1 - 1][y1] == nothing) {
                world[x1 - 1][y1] = wall;
            }
        }
    }

    /** Helper function for drawHallway, hallway goes right then up */
    public void drawRUHallway(TETile[][] world, int[] room1, int[] room2) {
        int x1 =  room1[0];
        int y1 = room1[1];
        int x2 = room2[0];
        int y2 = room2[1];

        int increment;

        if (y1 <= y2) {
            increment = 1;
        } else {
            increment = -1;
        }
        while (y1 != y2) {
            y1 += increment;
            world[x1][y1] = floor;
            if (world[x1 + 1][y1] == nothing) {
                world[x1 + 1][y1] = wall;
            }
            if (world[x1 - 1][y1] == nothing) {
                world[x1 - 1][y1] = wall;
            }
        }
        if (world[x1][y1 + increment] == nothing) {
            world[x1][y1 + increment] = wall;
        }
        if (x1 <= x2) {
            increment = 1;
        } else {
            increment = -1;
        }
        while (x1 != x2) {
            x1 += increment;
            world[x1][y1] = floor;
            if (world[x1][y1 + 1] == nothing) {
                world[x1][y1 + 1] = wall;
            }
            if (world[x1][y1 - 1] == nothing) {
                world[x1][y1 - 1] = wall;
            }
        }
        if (world[x1 + increment][y1] == nothing) {
            world[x1 + increment][y1] = wall;
        }
    }

    /** Finds closest room to room in the given grid number */
    public int findClosestRoom(int gridNumber) {
        int[] point = gridMap.get(gridNumber);
        Double closest = Double.MAX_VALUE;
        int returnNum = gridNumber;
        for (int number: gridMap.keySet()) {
            int[] coordinates = gridMap.get(number);
            Double xDiff = Math.pow(Math.abs(coordinates[0] - point[0]), 2);
            Double yDiff = Math.pow(Math.abs(coordinates[1] - point[1]), 2);
            Double distance = Math.sqrt(xDiff + yDiff);
            if (distance == 0) {
                continue;
            }
            if (distance < closest) {
                closest = distance;
                returnNum = number;
            }
        }
        return returnNum;
    }

    /** Finds closest pair of rooms between rooms in set a and rooms set b */
    public int[] findClosestRoomSet(Set<Integer> a, Set<Integer> b) {
        Double closest = Double.MAX_VALUE;
        int[] returnArray = new int[]{-1, -1};
        for (Integer aGridNumber : a) {
            int[] point = gridMap.get(aGridNumber);
            for (Integer bGridNumber : b) {
                int[] coordinates = gridMap.get(bGridNumber);
                Double xDiff = Math.pow(Math.abs(coordinates[0] - point[0]), 2);
                Double yDiff = Math.pow(Math.abs(coordinates[1] - point[1]), 2);
                Double distance = Math.sqrt(xDiff + yDiff);
                if (distance < closest) {
                    closest = distance;
                    returnArray[0] = aGridNumber;
                    returnArray[1] = bGridNumber;
                }
            }
        }
        return returnArray;
    }

    public void spawnEscapePod(TETile[][] world) {
        int x = 0;
        int y = 0;
        boolean foundSpot = false;
        while (!foundSpot) {
            x = RandomUtils.uniform(random, 1, Engine.WIDTH - 1);
            y = RandomUtils.uniform(random, 1, Engine.HEIGHT - 1);
            if (world[x][y] == wall) {
                if (world[x + 1][y] == floor
                        || world[x - 1][y] == floor
                        || world[x][y + 1] == floor
                        || world[x][y - 1] == floor) {
                    foundSpot = true;
                }
            }
        }
        world[x][y] = escapePod;
    }

    public void spawnFlashlight(TETile[][] world) {
        int x = 0;
        int y = 0;
        while (world[x][y] != floor) {
            x = RandomUtils.uniform(random, Engine.WIDTH);
            y = RandomUtils.uniform(random, Engine.HEIGHT);
        }
        world[x][y] = flashlight;
    }

    public static TETile floor() {
        return floor;
    }

    public static TETile wall() {
        return wall;
    }

    public static TETile nothing() {
        return nothing;
    }

    public static TETile player() {
        return player;
    }

    public Random getRandom() {
        return random;
    }

    public static TETile flashlight() {
        return flashlight;
    }

    public static TETile escapePod() {
        return escapePod;
    }
}
