package net.leng.maze.entities;

import net.leng.maze.screens.SettingPanel;
import net.leng.maze.util.ResourceDirectory;
import net.leng.maze.util.Logger;
import net.leng.maze.util.MazeMaker;
import net.leng.maze.screens.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

public class MazePlayer {
    private int x = 0;
    private int y = 0;
    private int healthLevel = 0;
    private int points = 0;
    private int facing = MazeMaker.DOWN;
    private int togglesLeft;
    private int justConsumed = 0; // 0 means nothing, 1 is cheese, 2 is webtoon, 3 is ultra webtoon
    private boolean isCollecting = true;
    private boolean hasWon = false;
    private MazeMaker mazeMaker;
    public MazePlayer(MazeMaker mazeMaker) {
        this.mazeMaker = mazeMaker;
    }

    public void setMazeMaker(MazeMaker mazeMaker) {
        this.mazeMaker = mazeMaker;
        resetLoc();
    }

    public void setHealth(int health) {
        healthLevel = health;
    }

    public void resetHealth() {
        healthLevel = 14 - (2 * SettingPanel.getDifficulty());
    }

    public int getHealth() {
        return healthLevel;
    }

    public void stopCollecting() {
        isCollecting = false;
    }

    public void startCollecting() {
        isCollecting = true;
    }

    public boolean isCollecting() {
        return isCollecting;
    }

    // the player is supposed to win whenever they reach location (size-1, size-1)

    public void moveLeft() {
        facing = MazeMaker.LEFT;
        if (mazeMaker.canMoveDirection(x, y, MazeMaker.LEFT)) {
            x--;
        }
    }

    public void moveRight() {
        if (x == mazeMaker.getSize() - 1 && y == mazeMaker.getSize() - 1) {
            hasWon = true;
        }
        facing = MazeMaker.RIGHT;
        if (mazeMaker.canMoveDirection(x, y, MazeMaker.RIGHT)) {
            x++;
        }
    }

    public void moveUp() {
        facing = MazeMaker.UP;
        if (mazeMaker.canMoveDirection(x, y, MazeMaker.UP)) {
            y--;
        }
    }

    public void moveDown() {
        facing = MazeMaker.DOWN;
        if (mazeMaker.canMoveDirection(x, y, MazeMaker.DOWN)) {
            y++;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void drawPlayer(Graphics g, int width, int height) {
        int size = mazeMaker.getSize();
        int interval = Math.min(width, height) / size;
        boolean shortHeight = height < width;
        int trimmedSize = interval * size;
        int added = (Math.max(height, width) - trimmedSize) / 2;

        int x1 = interval * x + (shortHeight ? added : 0) + 1;
        int y1 = interval * y + (shortHeight ? 0 : added) + 1;

        BufferedImage playerImage = generatePlayerImage();
        if (playerImage != null) {
            g.drawImage(playerImage, x1, y1, interval-2, interval-2, null);
        } else {
            g.setColor(new Color(139, 116, 77));
            // later draw a sprite thingy here
            g.fillOval(x1, y1, interval-2, interval-2);
        }
    }

    private BufferedImage generatePlayerImage() {
        if (hasWon) {
            return ResourceDirectory.ETHAN_WON;
        } else if (healthLevel < 0) {
            return ResourceDirectory.ETHAN_LOST;
        } else if (isCollecting) {
            if (justConsumed == 1) {
                return ResourceDirectory.ETHAN_ATE_CHEESE;
            } else if (justConsumed == 2) {
                return ResourceDirectory.ETHAN_WEBTOON;
            }
        }
        return switch (facing) {
            case MazeMaker.RIGHT -> ResourceDirectory.ETHAN_LEFT;
            case MazeMaker.LEFT -> ResourceDirectory.ETHAN_RIGHT;
            case MazeMaker.UP -> ResourceDirectory.ETHAN_BACK;
            default -> ResourceDirectory.ETHAN_FRONT;
        };
    }

    public void action(char c) {
        if (!Screen.hasNoMaze() && !hasWon && healthLevel >= 0) {
            justConsumed = 0;
            switch (c) {
                case 'w', 'W' -> moveUp();
                case 'a', 'A' -> moveLeft();
                case 's', 'S' -> moveDown();
                case 'd', 'D' -> moveRight();
                case 'x', 'X' -> {
                    int difficulty = SettingPanel.getDifficulty();
                    boolean orgCollecting = isCollecting;
                    if (difficulty < 2 || !isCollecting || points > 0)
                        isCollecting = !isCollecting;
                    if (points > 0 && orgCollecting) {
                        if (difficulty == 2) points--;
                        else if (difficulty > 2) points -= 2;
                    }
                }
            }
            if (isCollecting) {
                if (mazeMaker.hasGoodStory(x, y)) {
                    points += 5;
                    mazeMaker.removeItem(x, y);
                    justConsumed = 2;
                }
                if (mazeMaker.hasStory(x, y)) {
                    points++;
                    mazeMaker.removeItem(x, y);
                    justConsumed = 2;
                }
                if (mazeMaker.hasCheese(x, y)) {
                    if (points > 0) points--;
                    mazeMaker.removeItem(x, y);
                    healthLevel--;
                    justConsumed = 1;
                }
            }
        }
    }

    public void autoMove() {
        if (!mazeMaker.pathMade()) mazeMaker.pathFinder(x, y);
        int nId = mazeMaker.getNextPathId();
        if (nId < 0) {
            Logger.log("The maze solver has encountered an error");
            Screen.AUTO_SOLVE = false;
        } else {
            if (mazeMaker.hasGoodStory(x, y)) {
                isCollecting = true;
                mazeMaker.removeItem(x, y);
                points += 5;
            } else if (mazeMaker.hasStory(x, y)) {
                isCollecting = true;
                mazeMaker.removeItem(x, y);
                points++;
            } else if (mazeMaker.hasCheese(x, y)) {
                isCollecting = false;
            }

            int nx = nId % mazeMaker.getSize();
            int ny = nId / mazeMaker.getSize();
            if (nx > x) facing = MazeMaker.RIGHT;
            else if (nx < x) facing = MazeMaker.LEFT;
            else if (ny < y) facing = MazeMaker.UP;
            else facing = MazeMaker.DOWN;

            x = nx;
            y = ny;

            if (x == mazeMaker.getSize() - 1 && y == mazeMaker.getSize() - 1) {
                Screen.AUTO_SOLVE = false;
                hasWon = true;
            }
        }
    }

    public boolean hasWon() {
        return hasWon;
    }

    public boolean hasLost() {
        return healthLevel <= 0;
    }

    public int getPoints() {
        return points;
    }

    public void resetLoc() {
        points = 0;
        x = 0;
        y = 0;
        justConsumed = 0;
        facing = MazeMaker.DOWN;
        hasWon = false;
        isCollecting = true;
    }

    public boolean finishedMaze() {
        return x == mazeMaker.getSize() - 1 && y == mazeMaker.getSize() - 1;
    }
}
