package net.leng.maze.screens;

import net.leng.maze.util.MazeMaker;

import javax.swing.*;
import java.awt.*;

public class OptionPanel extends JPanel {
    static int option = 0;
    // 0 is none selected
    // 1 is dfs
    // 2 is kruskals
    // 3 is prims
    // 4 is bin tree
    static int mostRecentOption = 0;
    static final JSlider slider = makeSlider();
    OptionPanel(Screen frame) {
        setBackground(Color.BLACK);
        // algorithms
        JComboBox<String> box = Screen.createComboBox(new String[]{"<Select Maze Type>", "DFS", "Kruskal's", "Prim's", "Binary Tree"});
        box.addActionListener(l -> {
            reset();
            mostRecentOption = box.getSelectedIndex();
        });
        add(box);

        // this allows user to select maze type & size
        add(Screen.makeButton("Generate", l -> {
            reset();
            option = box.getSelectedIndex();
            GamePanel.MAKER = new MazeMaker(slider.getValue());
            GamePanel.PLAYER.setMazeMaker(GamePanel.MAKER);
            if (option > 0 && option < 5) frame.openScreen(Screen.GAME);
        }));

        // slider for maze size
        slider.addChangeListener(l -> {
            option = 0;
            repaint();
        });
        add(slider);

        add(Screen.makeButton("Go Back", l -> {
            frame.openScreen(Screen.START);
        }));
    }

    private void reset() {
        if (Screen.getFrames()[0] instanceof Screen screen) {
            screen.resetGrid();
            GamePanel.PLAYER.resetLoc();
        }
    }

    private static JSlider makeSlider() {
        JSlider slider = new JSlider();
        slider.setBackground(new Color(215, 245, 255));
        slider.setToolTipText("Maze Size");
        slider.setMinimum(5);
        slider.setMaximum(25);
        slider.setValue(5);
        return slider;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int sliderVal = slider.getValue();

        // maze drawer start
        int width = getWidth();
        int height = getHeight() - 200;
        int interval = Math.min(width, height) / sliderVal;
        boolean shortHeight = height < width;
        int trimmedSize = interval * sliderVal;
        int added = (Math.max(height, width) - trimmedSize) / 2;
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < sliderVal; i++) {
            int x1 = interval * i + (shortHeight ? added : 0);
            int x2 = x1 + interval;
            for (int j = 0; j < sliderVal; j++) {
                int y1 = interval * j + (shortHeight ? 0 : added);
                int y2 = y1 + interval;
                g.drawLine(x1, y1, x1, y2);
                g.drawLine(x2, y1, x2, y2);
                g.drawLine(x1, y1, x2, y1);
                g.drawLine(x1, y2, x2, y2);
            }
        }
        // maze drawer end
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        String mazeType = switch (OptionPanel.mostRecentOption) {
            case 1 -> "DFS";
            case 2 -> "Kruskal's";
            case 3 -> "Prim's";
            case 4 -> "Binary Tree";
            default -> "None";
        };
        g.drawString("Maze Size: " + sliderVal + "x" + sliderVal, (shortHeight ? added : 0), getHeight() - 180);
        g.drawString("Maze Type: " + mazeType, (shortHeight ? added : 0), getHeight() - 150);
    }
}
