package net.leng.maze.screens;

import net.leng.maze.util.ResourceDirectory;

import javax.swing.*;
import java.awt.*;

public class StartScreen extends JPanel {
    private final JButton createButton;
    private final JButton guideButton;
    private final JButton settingButton;
    private final JButton exitButton;

    StartScreen(Screen frame) {
        super();
        setBackground(new Color(77, 100, 115));
        setLayout(null);
        createButton = makeButton("Create Maze", Screen.OPTIONS, frame);
        guideButton = makeButton("Guide", Screen.GUIDE, frame);
        settingButton = makeButton("Settings", Screen.SETTINGS, frame);
        exitButton = Screen.makeButton("Exit Game", l -> System.exit(0));

        exitButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));

        createButton.setBounds(375, 200, 150, 36);
        guideButton.setBounds(375, 300, 150, 36);
        settingButton.setBounds(375, 400, 150, 36);
        exitButton.setBounds(375, 500, 150, 36);

        add(createButton);
        add(guideButton);
        add(settingButton);
        add(exitButton);
    }

    private JButton makeButton(String name, int screenId, Screen screen) {
        JButton button = Screen.makeButton(name, l -> screen.openScreen(screenId));
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int interval = Math.min(width, height);
        boolean shortHeight = height < width;
        int added = (Math.max(height, width) - interval) / 2;
        g.drawImage(ResourceDirectory.LOGO, (shortHeight ? added : 0), (shortHeight ? 0 : added), interval, interval, null);

        g.drawImage(ResourceDirectory.TEXT_LOGO, 20 + (getWidth() - 860) / 2, 10, 860, 122, null);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        createButton.setBounds(width/2 - 75, 200, 150, 36);
        guideButton.setBounds(width/2 - 75, 300, 150, 36);
        settingButton.setBounds(width/2 - 75, 400, 150, 36);
        exitButton.setBounds(width/2 - 75, 500, 150, 36);
    }
}
