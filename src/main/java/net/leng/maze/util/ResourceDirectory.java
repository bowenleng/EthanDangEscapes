package net.leng.maze.util;

import net.leng.maze.EthanDangMaze;
import net.leng.maze.screens.SettingPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;

public class ResourceDirectory {
    public static char UP_KEY = 'w';
    public static char DOWN_KEY = 's';
    public static char LEFT_KEY = 'a';
    public static char RIGHT_KEY = 'd';
    public static char COLLECT_KEY = 'x';

    private static final File SETTINGS = new File("settings.dat");

    public static Image getImage(String name) {
        try {
            ClassLoader loader = EthanDangMaze.class.getClassLoader();
            InputStream src = loader.getResourceAsStream("assets/images/" + name + ".png");
            if (src == null) {
                Logger.log("No image of " + name + ".png was found");
            } else {
                return ImageIO.read(src);
            }
        } catch (IOException ignored) {
            Logger.log("Cannot retrieve image for " + name + ".png");
        }
        return null;
    }

    public static void loadSettings() {
        try {
            Scanner scanner = new Scanner(SETTINGS);
            SettingPanel.FAST_FORWARD = scanner.hasNextInt() && scanner.nextInt() == 1;
            SettingPanel.DIFFICULTY = scanner.hasNextInt() ? scanner.nextInt() : 2;

            String keybinds = scanner.hasNext() ? scanner.next() : "wasdx";
            UP_KEY = keybinds.charAt(0);
            LEFT_KEY = keybinds.charAt(1);
            DOWN_KEY = keybinds.charAt(2);
            RIGHT_KEY = keybinds.charAt(3);
            COLLECT_KEY = keybinds.charAt(4);
        } catch (FileNotFoundException ignored) {
            Logger.log("No settings file was found");
        }
    }

    public static void saveSettings() {
        try {
            SETTINGS.delete();
            FileWriter writer = new FileWriter("settings.dat");

            int animate = SettingPanel.FAST_FORWARD ? 1 : 0;
            int difficulty = SettingPanel.DIFFICULTY;
            String keybinds = "" + UP_KEY + LEFT_KEY + DOWN_KEY + RIGHT_KEY + COLLECT_KEY;
            writer.write(animate + " " + difficulty + " " + keybinds);
            writer.close();
        } catch (Exception ignored) {
            Logger.log("Unable to save settings");
        }
    }

    public static String keycodeToName(char ch) {
        return switch (ch) {
            case KeyEvent.VK_ENTER -> "ENTER";
            case KeyEvent.VK_BACK_SPACE -> "BACKSPACE";
            case KeyEvent.VK_TAB -> "TAB";
            case KeyEvent.VK_CANCEL -> "CANCEL";
            case KeyEvent.VK_CLEAR -> "CLEAR";
            case KeyEvent.VK_SHIFT -> "SHIFT";
            case KeyEvent.VK_CONTROL -> "CTRL";
            case KeyEvent.VK_ALT -> "ALT";
            case KeyEvent.VK_PAUSE -> "PAUSE";
            case KeyEvent.VK_CAPS_LOCK -> "CAPS LOCK";
            case KeyEvent.VK_PAGE_UP -> "PG UP";
            case KeyEvent.VK_PAGE_DOWN -> "PG DOWN";
            case KeyEvent.VK_END -> "END";
            case KeyEvent.VK_HOME -> "HOME";
            case KeyEvent.VK_SPACE -> "SPACE";
            case KeyEvent.VK_LEFT -> "LEFT";
            case KeyEvent.VK_RIGHT -> "RIGHT";
            case KeyEvent.VK_UP -> "UP";
            case KeyEvent.VK_DOWN -> "DOWN";
            default -> "" + Character.toUpperCase(ch);
        };
    }

    public static final Image CHEESE = getImage("cheese");
    public static final Image WEBTOON = getImage("webtoon_story");
    public static final Image GOOD_WEBTOON = getImage("good_webtoon_story");

    public static final Image HEART = getImage("heart");
    public static final Image HALF_HEART = getImage("half_heart");

    public static final Image ETHAN_BACK = getImage("ethan_dang_back");
    public static final Image ETHAN_FRONT = getImage("ethan_dang_front");
    public static final Image ETHAN_LEFT = getImage("ethan_dang_left");
    public static final Image ETHAN_RIGHT = getImage("ethan_dang_right");

    public static final Image ETHAN_ATE_CHEESE = getImage("ethan_dang_ate_cheese");
    public static final Image ETHAN_WEBTOON = getImage("ethan_dang_webtoon");

    public static final Image ETHAN_LOST = getImage("ethan_dang_lost");
    public static final Image ETHAN_WON = getImage("ethan_dang_won");

    public static final Image IDK_IMAGE = getImage("idk_image");

    public static final Image LOGO = getImage("logo");
    public static final Image FAVICON = getImage("favicon");
}
