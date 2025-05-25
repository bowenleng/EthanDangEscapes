package net.leng.maze.screens;

import javax.swing.*;
import java.awt.*;

public class SettingPanel extends JPanel {
    static boolean FAST_FORWARD = false;
    private static int DIFFICULTY = 2;
    public SettingPanel(Screen frame) {
        setBackground(Color.BLACK);
        // this allows user to change keybinds and whether the maze generation animates
        JCheckBox checkBox = new JCheckBox("Don't Animate Generation");
        checkBox.setBackground(new Color(215, 245, 255));
        checkBox.addActionListener(l -> FAST_FORWARD = !FAST_FORWARD);
        add(checkBox);

        add(Screen.makeButton("Go Back", l -> frame.openScreen(Screen.START)));

        JComboBox<String> box = Screen.createComboBox(new String[]{"Effortless", "Easy", "Medium", "Hard", "XTREME"});
        box.setSelectedIndex(2);
        box.addActionListener(l -> {
            DIFFICULTY = box.getSelectedIndex();
            frame.difficultyChange();
            GamePanel.PLAYER.resetHealth();
        });
        add(box);
    }

    public static int getDifficulty() {
        return DIFFICULTY;
    }

    private void makeSettingFile() {
        // basic jist, an array of length 6 is stored
        // first index is whether to animate generation
        // 2 is up, 3 is down, 4 is left, 5 is right
        // 6 is the toggle collection button.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Screen.drawIdkImage(g, getWidth(), getHeight());
    }
}
