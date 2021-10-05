package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 82;
    public static final int HEIGHT = 42;
    private static String language = "English";
    private static int range = 3;
    private static int steps = 200;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {

        MainMenu game = new MainMenu(40, 40);
        game.initFrame(language);
        String seed = game.solicitInput(language);

        while (seed.equals("switch") || seed.equals("lore")) {
            if (seed.equals("lore")) {
                game.showLore(language);
            } else if (language.equals("English")) {
                language = "Spanish";
                game.initFrame(language);
            } else {
                language = "English";
                game.initFrame(language);
            }
            seed = game.solicitInput(language);
        }

        World world;
        long xPos = -1;
        long yPos = -1;
        boolean lightsOn = false;
        if (seed.equals("l")) {
            File cwd = new File(System.getProperty("user.dir"));
            File saveFile = Utils.join(cwd, "saveFile.txt");
            if (!saveFile.exists()) {
                System.exit(0);
            }
            long[] save = Utils.readObject(saveFile, long[].class);
            seed = save[0] + "";
            xPos = save[1];
            yPos = save[2];
            if (save[3] == 1) {
                lightsOn = true;
            }
            range = (int) save[4];
            steps = (int) save[5];
        }

        WorldGeneration worldGenerator = new WorldGeneration();
        TETile[][] newTerrain = worldGenerator.initWorld(Long.parseLong(seed),
                WIDTH, HEIGHT, range <= 3);
        world = new World(newTerrain, worldGenerator.getRandom(), Long.parseLong(seed));
        world.spawnPlayer((int) xPos, (int) yPos);

        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16 + 80);
        ter.initialize(WIDTH, HEIGHT + 5, 0, 0);
        updateHud(world, lightsOn);

        boolean colonPressed = false;
        char c;
        int mouseX = 0;
        int mouseY = 0;
        while (true) {
            if (steps == 0) {
                winCondition(false);
            }
            if (StdDraw.hasNextKeyTyped()) {
                c = StdDraw.nextKeyTyped();
            } else if (mouseX != (int) StdDraw.mouseX() || mouseY != (int) StdDraw.mouseY()) {
                updateHud(world, lightsOn);
                mouseX = (int) StdDraw.mouseX();
                mouseY = (int) StdDraw.mouseY();
                continue;
            } else {
                continue;
            }
            boolean[] temp = processInput(c, world, colonPressed, lightsOn);
            colonPressed = temp[0];
            lightsOn = temp[1];
            updateHud(world, lightsOn);
        }
    }

    public static boolean[] processInput(char c, World world,
                                         boolean colonPressed, boolean lightsOn) {
        if (c == 'w' || c == 'a' || c == 's' || c == 'd') {
            range = world.movePlayer(c, range);
            steps -= 1;
            if (range == -1) {
                winCondition(true);
            }
            return new boolean[]{false, lightsOn};
        } else if (c == 'q' || c == 'Q') {
            if (colonPressed) {
                world.save(lightsOn, range, steps);
                System.exit(0);
            }
            return new boolean[]{false, lightsOn};
        } else if (c == 't' || c == 'T') {
            return new boolean[]{false, !lightsOn};
        } else {
            return new boolean[]{c == ':', lightsOn};
        }
    }

    public void updateHud(World world, boolean lightsOn) {
        StdDraw.clear(Color.BLACK);

        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        TETile tile;
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            tile = null;
        } else {
            tile = world.world()[x][y];
        }

        String tileName;
        if (tile == Tileset.DARKGRAY) {
            tileName = "Wall";
            if (language.equals("Spanish")) {
                tileName = "Pared";
            }
        } else if (tile == Tileset.GRAY) {
            tileName = "Floor";
            if (language.equals("Spanish")) {
                tileName = "Suelo";
            }
        } else if (tile == Tileset.FLOOR) {
            tileName = "Nothing (Space)";
            if (language.equals("Spanish")) {
                tileName = "Nada (Espacio Exterior)";
            }
        } else if (tile == Tileset.AVATAR) {
            tileName = "Player";
            if (language.equals("Spanish")) {
                tileName = "El Jugador";
            }
        } else if (tile == Tileset.FLASHLIGHT) {
            tileName = "Flashlight";
            if (language.equals("Spanish")) {
                tileName = "Linterna";
            }
        } else if (tile == Tileset.ESCAPEPOD) {
            tileName = "Escape Pod";
            if (language.equals("Spanish")) {
                tileName = "Cápsula de Escape";
            }
        } else {
            tileName = "";
        }

        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        StdDraw.text(11, 44, tileName);
        if (language.equals("English")) {
            StdDraw.text(71, 44, "Remaining Steps: " + steps);
        } else {
            StdDraw.text(71, 44, "Pasos Restantes: " + steps);
        }
        font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);
        if (lightsOn) {
            ter.renderFrame(world.world());
        } else {
            ter.renderFrame(lineOfSight(world));
        }
        StdDraw.show();
    }

    public static TETile[][] lineOfSight(World world) {
        TETile[][] darkWorld = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                darkWorld[x][y] = Tileset.NOTHING;
            }
        }
        int playerX = world.playerXPos();
        int playerY = world.playerYPos();
        illuminate(world.world(), darkWorld, playerX, range, playerY);
        for (int i = range, j = 1; i > 0; i--, j++) {
            illuminate(world.world(), darkWorld, playerX, i, playerY + j);
            illuminate(world.world(), darkWorld, playerX, i, playerY - j);
        }
        return darkWorld;
    }

    public static void illuminate(TETile[][] world, TETile[][] darkWorld,
                                  int playerX, int xRange, int row) {
        if (row >= HEIGHT || row < 0) {
            return;
        }
        for (int x = playerX - xRange; x <= playerX + xRange; x++) {
            if (x < 0 || x >= WIDTH) {
                continue;
            }
            darkWorld[x][row] = world[x][row];
        }
    }

    public static void winCondition(boolean won) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        if (won) {
            if (language.equals("English")) {
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "YOU WIN!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "¡TÚ GANAS!");
            }
        } else {
            if (language.equals("English")) {
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "YOU LOSE!");
            } else {
                StdDraw.text(WIDTH / 2, HEIGHT / 2, "¡TÚ PIERDES!");
            }
        }
        font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        if (language.equals("English")) {
            StdDraw.text(WIDTH / 2, 15, "Press (Q) to quit");
        } else {
            StdDraw.text(WIDTH / 2, 15, "Presione (Q) para salir");
        }
        StdDraw.show();
        while (true) {
            File cwd = new File(System.getProperty("user.dir"));
            File saveFile = new File(cwd, "saveFile.txt");
            Utils.restrictedDelete(saveFile);
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'q' || c == 'Q') {
                    System.exit(0);
                }
            }
        }
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     * @source //https://www.javatpoint.com/java-char-to-string for adding char to String
     */
    public TETile[][] interactWithInputString(String input) {
        String seed = "";
        long xPos = -1;
        long yPos = -1;
        int i = 1;
        boolean lightsOn = false;
        if (input.charAt(0) == 'l' || input.charAt(0) == 'L') {
            File cwd = new File(System.getProperty("user.dir"));
            File saveFile = Utils.join(cwd, "saveFile.txt");
            long[] save = Utils.readObject(saveFile, long[].class);
            seed = save[0] + "";
            xPos = save[1];
            yPos = save[2];
            if (save[3] == 1) {
                lightsOn = true;
            }
            range = (int) save[4];
            steps = (int) save[5];
        } else {
            while (input.charAt(i) != 's' && input.charAt(i) != 'S') {
                seed += String.valueOf(input.charAt(i));
                i++;
            }
            i++;
        }

        WorldGeneration worldGenerator = new WorldGeneration();
        TETile[][] newTerrain = worldGenerator.initWorld(Long.parseLong(seed),
                WIDTH, HEIGHT, range <= 3);
        World world = new World(newTerrain, worldGenerator.getRandom(), Long.parseLong(seed));
        world.spawnPlayer((int) xPos, (int) yPos);

        while (i < input.length()) {
            char c = input.charAt(i);
            if (c == ':') {
                i++;
                c = input.charAt(i);
                if (c == 'q' || c == 'Q') {
                    world.save(lightsOn, range, steps);
                    break;
                }
            }
            if (c == 'a' || c == 'A' || c == 's' || c == 'S' || c == 'w'
                    || c == 'W' || c == 'd' || c == 'D') {
                int temp = range;
                range = world.movePlayerString(c, range);
                if (range < 0) {
                    range = temp;
                }
                steps -= 1;
            } else if (c == 't' || c == 'T') {
                lightsOn = !lightsOn;
            }
            i++;
        }
        return world.world();
    }
}
