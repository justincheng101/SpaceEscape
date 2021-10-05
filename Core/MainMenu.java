package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class MainMenu {
    /** The width of the window of this menu. */
    private int width;
    /** The height of the window of this menu. */
    private int height;
    /** The lore */
    private final String lore1 = "You’re a confused adventurer "
            + "who woke up on a mysterious spacecraft.";
    private final String lore2 = "You don’t know where you are or what’s going on.";
    private final String lore3 = "You see a timer on the wall with a "
            + "flashing message under it. It reads:";
    private final String lore4 = "“Self destruct in 200 seconds. "
            + "Head for the escape pods before it’s too late!”";
    private final String lore5 = "“Holy cannoli!” You exclaim. “I’ve got to get out of here!”";
    private final String lore6 = "Instructions: Find the escape pod in";
    private final String lore7 = "under 200 steps in order to escape safely.";
    private final String lore8 = "If you don’t, you’ll blow up with the ship.";
    private final String lore9 = "There are flashlights you can find throughout the";
    private final String lore10 = "map that will increase your range of visibility.";

    private final String sLore1 = "Eres un aventurero confundido que se despertó "
            + "en una misteriosa nave espacial.";
    private final String sLore2 = "No sabes dónde estás ni qué está pasando.";
    private final String sLore3 = "Ves un temporizador en la pared con un "
             + "mensaje parpadeante debajo. Se lee:";
    private final String sLore4 = "“Autodestruirse en 200 segundos.”";
    private final String sLore5 = "“¡Dirígete a las cápsulas de escape "
            + "antes de que sea demasiado tarde!”";
    private final String sLore6 = "“¡Santo cannoli!”Exclamas. “¡Tengo que salir de aquí!”";
    private final String sLore7 = "Instrucciones: Encuentra la cápsula de escape en ";
    private final String sLore8 = "menos de 200 pasos para escapar de manera segura.";
    private final String sLore9 = "Si no lo hace, volará con el barco.";
    private final String sLore10 = "Hay linternas que puede encontrar en todo";
    private final String sLore11 = "el mapa que aumentarán su rango de visibilidad.";

    public MainMenu(int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void initFrame(String language) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco", Font.BOLD, 35);
        StdDraw.setFont(font);
        if (language.equals("English")) {
            StdDraw.text(20, 30, "☆☆☆ SPACE ESCAPE ☆☆☆");
        } else if (language.equals("Spanish")) {
            StdDraw.text(20, 30, "☆☆☆ ESCAPE ESPACIAL ☆☆☆");
        }
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        if (language.equals("English")) {
            StdDraw.text(20, 20, "New Game (N)");
            StdDraw.text(20, 18.5, "Load Game (L)");
            StdDraw.text(20, 17, "Quit (Q)");
            StdDraw.text(20, 15.5, "Español (X)");
            StdDraw.text(20, 14, "Lore (O)");
        } else if (language.equals("Spanish")) {
            StdDraw.text(20, 20, "Nuevo Juego (N)");
            StdDraw.text(20, 18.5, "Juego de Carga (L)");
            StdDraw.text(20, 17, "Dejar (Q)");
            StdDraw.text(20, 15.5, "English (X)");
            StdDraw.text(20, 14, "Historia (O)");
        }
        StdDraw.show();
    }

    public String getInput() {
        String input = "";
        boolean press = false;
        while (!press) {
            if (StdDraw.hasNextKeyTyped()) {
                input += StdDraw.nextKeyTyped();
                press = true;
            }
        }
        return input;
    }

    public String solicitInput(String language) {
        while (true) {
            String input = getInput();
            if (input.equals("n") || input.equals("N")) {
                return getSeed(language);
            } else if (input.equals("l") || input.equals("L")) {
                return "l";
            } else if (input.equals("q") || input.equals("Q")) {
                System.exit(0);
            } else if (input.equals("x") || input.equals("X")) {
                return "switch";
            } else if (input.equals("o") || input.equals("O")) {
                return "lore";
            }
        }
    }

    public void drawFrame(String s, String language) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(width / 2, height / 2, s);
        if (language.equals("English")) {
            StdDraw.text(width / 2, 36, "Input Seed:");
        } else if (language.equals("Spanish")) {
            font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            StdDraw.text(width / 2, 36, "Introduce el Número del Mundo:");
        }
        StdDraw.show();
    }

    public String getSeed(String language) {
        StdDraw.clear(Color.BLACK);
        StdDraw.show();
        String input = "";
        ArrayList numbers = new ArrayList();
        for (char i = '0'; i <= '9'; i++) {
            numbers.add(i);
        }
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 's' || c == 'S') {
                    StdDraw.clear(Color.BLACK);
                    return input;
                } else if (!numbers.contains(c)) {
                    continue;
                }
                input += Character.toString(c);
            }
            drawFrame(input, language);
        }
    }

    public void showLore(String language) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        if (language.equals("English")) {
            StdDraw.text(width / 2, 38, "Back (B)");
            font = new Font("Monaco", Font.BOLD, 12);
            StdDraw.setFont(font);
            StdDraw.text(width / 2, 30, lore1);
            StdDraw.text(width / 2, 29, lore2);
            StdDraw.text(width / 2, 22, lore3);
            StdDraw.text(width / 2, 21, lore4);
            StdDraw.text(width / 2, 15, lore5);
            StdDraw.text(width / 2, 9, lore6);
            StdDraw.text(width / 2, 8, lore7);
            StdDraw.text(width / 2, 7, lore8);
            StdDraw.text(width / 2, 6, lore9);
            StdDraw.text(width / 2, 5, lore10);
        } else if (language.equals("Spanish")) {
            StdDraw.text(width / 2, 38, "Volver (B)");
            font = new Font("Monaco", Font.BOLD, 12);
            StdDraw.setFont(font);
            StdDraw.text(width / 2, 30, sLore1);
            StdDraw.text(width / 2, 29, sLore2);
            StdDraw.text(width / 2, 22, sLore3);
            StdDraw.text(width / 2, 21, sLore4);
            StdDraw.text(width / 2, 20, sLore5);
            StdDraw.text(width / 2, 14, sLore6);
            StdDraw.text(width / 2, 8, sLore7);
            StdDraw.text(width / 2, 7, sLore8);
            StdDraw.text(width / 2, 6, sLore9);
            StdDraw.text(width / 2, 5, sLore10);
            StdDraw.text(width / 2, 4, sLore11);
        }
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'b' || c == 'B') {
                    initFrame(language);
                    break;
                }
            }
        }
    }
}
