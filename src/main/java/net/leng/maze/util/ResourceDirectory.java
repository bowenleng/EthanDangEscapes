package net.leng.maze.util;

import net.leng.maze.EthanDangMaze;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class ResourceDirectory {
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
