package net.leng.maze.screens;

import net.leng.maze.entities.MazePlayer;
import net.leng.maze.util.MazeMaker;
import net.leng.maze.util.ResourceDirectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.BooleanSupplier;

public class GamePanel extends JPanel {
    static MazePlayer PLAYER = null;
    static MazeMaker MAKER;
    static long startTime;
    static long endTime;
    GamePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(720, 720));
        MAKER = new MazeMaker(5);
        PLAYER = new MazePlayer(MAKER);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Screen.NO_MAZE && !PLAYER.hasWon()) {
                    PLAYER.action(e.getKeyChar());
                    if (PLAYER.hasWon()) {
                        endTime = System.currentTimeMillis();
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        PLAYER.setHealth(14 - (2 * SettingPanel.getDifficulty()));
    }

    public void regenColor() {
        MAKER.regenerateColor();
    }

    public void redrawMatrix(int size) {
        if (MAKER.getSize() == size) {
            MAKER.reset();
        } else {
            MAKER = new MazeMaker(size, MAKER);
            PLAYER.setMazeMaker(MAKER);
        }
        Screen.NO_MAZE = true;
        repaint();
    }

    public void resetMatrix() {
        MAKER.reset();
        repaint();
    }

    @Override
    public void repaint() {
        super.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int height = getHeight();
        int width = getWidth();
        int interval = Math.min(width, height);
        boolean shortHeight = height < width;
        int added = (Math.max(height, width) - interval) / 2;
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 120);
        g.setFont(font);
        if (PLAYER.hasWon()) {
            g.drawImage(ResourceDirectory.ETHAN_WON, (shortHeight ? added : 0), (shortHeight ? 0 : added), interval, interval, null);
            g.setColor(BottomPanel.activeColor);
            String playerStat = "YOU WON!";
            g.drawString(playerStat, (shortHeight ? added : 0) + (interval - getStringWidth(g, playerStat, font))/2, 100);
        } else if (PLAYER.hasLost()) {
            g.drawImage(ResourceDirectory.ETHAN_LOST, (shortHeight ? added : 0), (shortHeight ? 0 : added), interval, interval, null);
            g.setColor(BottomPanel.badColor);
            String playerStat = "YOU LOST!";
            g.drawString(playerStat, (shortHeight ? added : 0) + (interval - getStringWidth(g, playerStat, font))/2, 100);
        } else {
            MAKER.setHeight(height);
            MAKER.setWidth(width);
            MAKER.draw(g);
            if (OptionPanel.option == 1) {
                runMaze(OptionPanel.option, () -> MAKER.dfs());
            } else if (OptionPanel.option == 2) {
                runMaze(OptionPanel.option, () -> MAKER.kruskals());
            } else if (OptionPanel.option == 3) {
                runMaze(OptionPanel.option, () -> MAKER.prims());
            } else if (OptionPanel.option == 4) {
                runMaze(OptionPanel.option, () -> MAKER.binaryTree());
            } else if (Screen.AUTO_SOLVE && !PLAYER.hasWon() && !PLAYER.hasLost()) {
                PLAYER.autoMove();
                PLAYER.drawPlayer(g, getWidth(), getHeight());
            } else if (!Screen.NO_MAZE) {
                if (Screen.CAN_FOCUS) focus();
                PLAYER.drawPlayer(g, getWidth(), getHeight());
            }
        }
    }

    public static int getStringWidth(Graphics g, String text, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    public void focus() {
        requestFocus();
        requestFocusInWindow();
    }

    private void runMaze(int val, BooleanSupplier sup) {
        OptionPanel.mostRecentOption = val;
        if (SettingPanel.FAST_FORWARD) {
            boolean stopped = sup.getAsBoolean();
            while (!stopped) {
                stopped = sup.getAsBoolean();
            }
            OptionPanel.option = 0;
            Screen.NO_MAZE = false;
            Screen.CAN_FOCUS = true;
            MAKER.generateItems();
            startTime = System.currentTimeMillis();
        } else if (sup.getAsBoolean()) {
            OptionPanel.option = 0;
            Screen.NO_MAZE = false;
            Screen.CAN_FOCUS = true;
            MAKER.generateItems();
            startTime = System.currentTimeMillis();
        }
    }

    static class BottomPanel extends JPanel {
        final static Color activeColor = new Color(135, 255, 114);
        final static Color idleColor = new Color(153, 208, 232);
        final static Color badColor = new Color(255, 84, 84);
        private boolean hasButton;
        private final JButton changeButon = Screen.makeButton("Change Palette", l -> {
            if (Screen.getFrames()[0] instanceof Screen screen && screen.getContentPane().getComponent(0) instanceof GamePanel drawing) {
                drawing.regenColor();
                drawing.repaint();
            }
        });
        private boolean noChangeButton;

        public static final JButton SOLVER = Screen.makeButton("Solve", l -> {
            if (!Screen.NO_MAZE) Screen.AUTO_SOLVE = true;
        });

        BottomPanel(Screen frame) {
            setBackground(new Color(66, 112, 131));
            setPreferredSize(new Dimension(600, 200));

            if (SettingPanel.getDifficulty() == 0) {
                add(SOLVER);
            }
            add(Screen.makeButton("Leave Game", l -> {
                reset();
                frame.openScreen(Screen.OPTIONS);
            }));

            add(changeButon);
            noChangeButton = false;
        }

        public void removeButton() {
            if (hasButton) {
                remove(SOLVER);
                hasButton = false;
            }
        }

        public void addButton() {
            if (!hasButton) {
                add(SOLVER);
                hasButton = true;
            }
        }

        private void reset() {
            if (Screen.getFrames()[0] instanceof Screen screen) {
                screen.resetGrid();
                PLAYER.resetLoc();
                PLAYER.resetHealth();
                OptionPanel.mostRecentOption = 0;
                Screen.NO_MAZE = true;
                Screen.AUTO_SOLVE = false;
                if (noChangeButton) {
                    add(changeButon);
                    noChangeButton = false;
                }
            }
        }

        private String timeFormat(long time) {
            int div = (int)time % 100;
            int sec = (int)(time / 100 % 60);
            int min = (int)(time / 6000 % 60);
            int hr = (int)(time / 360000);
            String divStr = (div < 10 ? "0" : "") + div;
            String secStr = (sec < 10 ? "0" : "") + sec;
            String minStr = (min < 10 ? "0" : "") + min;
            String hrStr = (hr < 10 ? "0" : "") + hr;
            return (hr == 0 ? "" : hrStr + ":") + minStr + ":" + secStr + "." + divStr;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
            g.setFont(font);
            g.setColor(Color.WHITE);
            if (PLAYER.hasWon()) {
                int width = getWidth();
                int interval = Math.min(width, 200);
                boolean shortHeight = 200 < width;
                int added = (Math.max(200, width) - interval) / 2;

                long timeLapsed = (endTime - startTime) / 10;
                String time = "Time taken: " + timeFormat(timeLapsed) + "!";
                g.drawString(time, (shortHeight ? added : 0) + (interval - getStringWidth(g, time, font))/2, 50);

                String points = "Points: " + PLAYER.getPoints();
                g.drawString(points, (shortHeight ? added : 0) + (interval - getStringWidth(g, points, font))/2, 70);

                String health = "Health Remaining: " + PLAYER.getHealth();
                g.drawString(health, (shortHeight ? added : 0) + (interval - getStringWidth(g, health, font))/2, 90);

                remove(changeButon);
                noChangeButton = true;
            } else if (!PLAYER.hasLost()) {
                //draws player hearts
                if (!Screen.NO_MAZE) {
                    float health = PLAYER.getHealth() / 2f;
                    for (int i = 0; i < health; i++) {
                        g.drawImage(i + 1 > health ? ResourceDirectory.HALF_HEART : ResourceDirectory.HEART, 10 + (20 * i), 10, 18, 18, null);
                    }
                }

                // draw the messaging
                boolean isRunning = OptionPanel.option != 0;
                int val = OptionPanel.slider.getValue();
                int maxW = getWidth() - 10;
                String mazeType = switch (OptionPanel.mostRecentOption) {
                    case 1 -> "DFS";
                    case 2 -> "Kruskal's";
                    case 3 -> "Prim's";
                    case 4 -> "Binary Tree";
                    default -> "None";
                };
                String mazeGen = "Maze Type: " + mazeType;
                g.drawString(mazeGen, maxW - getStringWidth(g, mazeGen, font), getHeight() - 40);
                String sizeStr = "Size: " + val + "x" + val;
                g.drawString(sizeStr, maxW - getStringWidth(g, sizeStr, font), getHeight() - 10);
                if (!Screen.NO_MAZE) {
                    String locStr = "Location: (" + PLAYER.getX() + ", " + PLAYER.getY() + ")";
                    g.drawString(locStr, 10, getHeight() - 10);

                    String ptStr = "Points: ";
                    g.drawString(ptStr, 10, getHeight() - 40);
                    g.drawString("" + Math.max(PLAYER.getPoints(), 0), 10 + getStringWidth(g, ptStr, font), getHeight() - 40);

                    if (PLAYER.hasWon()) {
                        g.setColor(activeColor);
                        g.drawString("You have won!", 10, getHeight() - 70);
                    } else if (PLAYER.hasLost()) {
                        g.setColor(badColor);
                        g.drawString("You have lost", 10, getHeight() - 70);
                    } else {
                        String status = PLAYER.isCollecting() ? "Collecting" : "Not collecting";
                        g.setColor(PLAYER.isCollecting() ? activeColor : idleColor);
                        g.drawString(status, 10, getHeight() - 70);
                    }
                }
                if (!SettingPanel.FAST_FORWARD && isRunning) {
                    g.setColor(activeColor);
                    g.drawString("Generating maze...", maxW - 180, getHeight() - 70);
                }
            }
        }
    }
}
