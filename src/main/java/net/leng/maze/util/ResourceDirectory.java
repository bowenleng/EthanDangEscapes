package net.leng.maze.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ResourceDirectory {
    public static BufferedImage getImage(String name) {
        try {
            return ImageIO.read(new File("src/main/resources/assets/images/" + name + ".png"));
        } catch (IOException ignored) {
            Logger.log("Cannot retrieve image for " + name + ".png");
            return null;
        }
    }

    public static final BufferedImage CHEESE = getImage("cheese");
    public static final BufferedImage WEBTOON = getImage("webtoon_story");
    public static final BufferedImage GOOD_WEBTOON = getImage("good_webtoon_story");

    public static final BufferedImage HEART = getImage("heart");
    public static final BufferedImage HALF_HEART = getImage("half_heart");

    public static final BufferedImage ETHAN_BACK = getImage("ethan_dang_back");
    public static final BufferedImage ETHAN_FRONT = getImage("ethan_dang_front");
    public static final BufferedImage ETHAN_LEFT = getImage("ethan_dang_left");
    public static final BufferedImage ETHAN_RIGHT = getImage("ethan_dang_right");

    public static final BufferedImage ETHAN_ATE_CHEESE = getImage("ethan_dang_ate_cheese");
    public static final BufferedImage ETHAN_WEBTOON = getImage("ethan_dang_webtoon");

    public static final BufferedImage ETHAN_LOST = getImage("ethan_dang_lost");
    public static final BufferedImage ETHAN_WON = getImage("ethan_dang_won");

    public static final BufferedImage IDK_IMAGE = getImage("idk_image");

    public static final BufferedImage LOGO = getImage("logo");
    public static final BufferedImage TEXT_LOGO = getImage("text_logo");
    public static final BufferedImage FAVICON = getImage("favicon");
}
