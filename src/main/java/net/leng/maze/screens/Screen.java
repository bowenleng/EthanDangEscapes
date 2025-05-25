package net.leng.maze.screens;

import net.leng.maze.util.ResourceDirectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Java version: 17
 * **/
public class Screen extends JFrame {
    public static final int START = 0;
    public static final int SETTINGS = 1;
    public static final int OPTIONS = 2;
    public static final int GAME = 3;
    public static final int GUIDE = 4;

    static boolean NO_MAZE = true;
    static boolean CAN_FOCUS = false;
    public static boolean AUTO_SOLVE = false;

    private final JPanel drawer;
    private final JPanel starter;
    private final GamePanel.BottomPanel bottomBar;
    private final JPanel settingScreen;
    private final JPanel optionScreen;
    private final JPanel guideScreen;
    private Screen() {
        super("Ethan Dang Escapes!");
        setPreferredSize(new Dimension(900, 900));
        setMinimumSize(new Dimension(900, 820));
        setVisible(true);

        if (ResourceDirectory.FAVICON != null) setIconImage(ResourceDirectory.FAVICON);
        drawer = new GamePanel();
        starter = new StartScreen(this);
        bottomBar = new GamePanel.BottomPanel(this);
        settingScreen = new SettingPanel(this);
        optionScreen = new OptionPanel(this);
        guideScreen = new GuidePanel(this);
        getContentPane().add(starter);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Timer timer = new Timer(20, l -> {
            for (Component c : getContentPane().getComponents()) {
                c.repaint();
            }
            if (GamePanel.PLAYER.finishedMaze()) CAN_FOCUS = false;
        });
        timer.start();
    }

    public void difficultyChange() {
        if (SettingPanel.getDifficulty() > 0) {
            bottomBar.removeButton();
        } else {
            bottomBar.addButton();
        }
    }

    public void openScreen(int screenId) {
        getContentPane().removeAll();
        switch (screenId) {
            case SETTINGS -> getContentPane().add(settingScreen);
            case OPTIONS -> getContentPane().add(optionScreen);
            case GAME -> {
                getContentPane().add(drawer, BorderLayout.CENTER);
                getContentPane().add(bottomBar, BorderLayout.SOUTH);
            }
            case GUIDE -> getContentPane().add(guideScreen);
            default -> getContentPane().add(starter);

        }
        validate();
    }

    public void redrawGrid(int size) {
        if (getContentPane().getComponent(0) instanceof GamePanel drawing) {
            drawing.redrawMatrix(size);
        }
    }

    public void resetGrid() {
        if (getContentPane().getComponent(0) instanceof GamePanel drawing) {
            drawing.resetMatrix();
        }
    }

    @Override
    public void repaint() {
        if (getContentPane().getComponent(0) instanceof GamePanel drawing) {
            drawing.repaint();
        }
    }

    public static boolean hasNoMaze() {
        return NO_MAZE;
    }

    public static void drawIdkImage(Graphics g, int width, int height) {
        int interval = Math.min(width, height);
        boolean shortHeight = height < width;
        int added = (Math.max(height, width) - interval) / 2;
        g.drawImage(ResourceDirectory.IDK_IMAGE, (shortHeight ? added : 0), (shortHeight ? 0 : added), interval, interval, null);
    }

    public static JButton makeButton(String name, ActionListener l) {
        JButton button = new JButton(name) {
            @Override
            protected void paintBorder(Graphics g) {
                super.paintBorder(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), 3);
                g.fillRect(0, 0, 3, getHeight());

                g.setColor(new Color(111, 166, 189));
                g.fillRect(getWidth(), 0, getWidth(), 3);
                g.fillRect(0, getHeight(), 3, getHeight());
            }
        };
        button.setBackground(new Color(153, 208, 232));
        button.addActionListener(l);
        return button;
    }

    public static JComboBox<String> createComboBox(String[] options) {
        JComboBox<String> box = new JComboBox<>(options);
        box.setBackground(new Color(215, 245, 255));
        box.getComponent(0).setBackground(new Color(153, 208, 232));
        return box;
    }

    public static void run() {
        Screen screen = new Screen();
        screen.pack();
    }
}
