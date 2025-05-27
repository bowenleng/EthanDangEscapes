package net.leng.maze.screens;

import net.leng.maze.util.ResourceDirectory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SettingPanel extends JPanel {
    public static boolean FAST_FORWARD = false;
    public static int DIFFICULTY = 2;

    private int keyLabelAccessed = -1;

    private final int[] labelPos = new int[]{225, 525, 100, 200, 300};
    // indices, 0 is first col, 1 is second col, 2 is first row, 3 is second row, 4 is third row.
    public SettingPanel(Screen frame) {
        setBackground(Color.BLACK);

        // this allows user to change keybinds and whether the maze generation animates
        JCheckBox checkBox = new JCheckBox("Don't Animate Generation");
        checkBox.setBackground(new Color(215, 245, 255));
        checkBox.addActionListener(l -> FAST_FORWARD = !FAST_FORWARD);
        if (FAST_FORWARD) checkBox.setSelected(true);
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

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (withinLabel1(x, y)) keyLabelAccessed = 0;
                else if (withinLabel2(x, y)) keyLabelAccessed = 1;
                else if (withinLabel3(x, y)) keyLabelAccessed = 2;
                else if (withinLabel4(x, y)) keyLabelAccessed = 3;
                else if (withinLabel5(x, y)) keyLabelAccessed = 4;
                else keyLabelAccessed = -1;
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        addKeyListener(new KeyListener() {
            boolean typed = false;
            @Override
            public void keyTyped(KeyEvent e) {
                switch (keyLabelAccessed) {
                    case 0 -> ResourceDirectory.UP_KEY = e.getKeyChar();
                    case 1 -> ResourceDirectory.LEFT_KEY = e.getKeyChar();
                    case 2 -> ResourceDirectory.DOWN_KEY = e.getKeyChar();
                    case 3 -> ResourceDirectory.RIGHT_KEY = e.getKeyChar();
                    case 4 -> ResourceDirectory.COLLECT_KEY = e.getKeyChar();
                }
                repaint();
                typed = true;
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (typed) {
                    typed = false;
                } else {
                    switch (keyLabelAccessed) {
                        case 0 -> ResourceDirectory.UP_KEY = (char)e.getKeyCode();
                        case 1 -> ResourceDirectory.LEFT_KEY = (char)e.getKeyCode();
                        case 2 -> ResourceDirectory.DOWN_KEY = (char)e.getKeyCode();
                        case 3 -> ResourceDirectory.RIGHT_KEY = (char)e.getKeyCode();
                        case 4 -> ResourceDirectory.COLLECT_KEY = (char)e.getKeyCode();
                    }
                    repaint();
                }
            }
        });
    }

    private boolean withinLabel1(int x, int y) {
        int x1 = labelPos[0];
        int y1 = labelPos[2];
        int x2 = x1 + 150;
        int y2 = y1 + 32;
        return x > x1 && x < x2 && y > y1 && y < y2;
    }

    private boolean withinLabel2(int x, int y) {
        int x1 = labelPos[1];
        int y1 = labelPos[2];
        int x2 = x1 + 150;
        int y2 = y1 + 32;
        return x > x1 && x < x2 && y > y1 && y < y2;
    }

    private boolean withinLabel3(int x, int y) {
        int x1 = labelPos[0];
        int y1 = labelPos[3];
        int x2 = x1 + 150;
        int y2 = y1 + 32;
        return x > x1 && x < x2 && y > y1 && y < y2;
    }

    private boolean withinLabel4(int x, int y) {
        int x1 = labelPos[1];
        int y1 = labelPos[3];
        int x2 = x1 + 150;
        int y2 = y1 + 32;
        return x > x1 && x < x2 && y > y1 && y < y2;
    }

    private boolean withinLabel5(int x, int y) {
        int x1 = labelPos[0];
        int y1 = labelPos[4];
        int x2 = x1 + 150;
        int y2 = y1 + 32;
        return x > x1 && x < x2 && y > y1 && y < y2;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        labelPos[0] = width / 2 - 225;
        labelPos[1] = width / 2 + 75;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Screen.drawIdkImage(g, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        g.drawString("Keybinds", getWidth()/2 - 36, 75);

        g.setColor(new Color(215, 245, 255));

        int col1 = labelPos[0];
        int col2 = labelPos[1];

        int row1 = labelPos[2];
        int row2 = labelPos[3];
        int row3 = labelPos[4];

        // drawing labels
        g.fillRect(col1, row1, 150, 32);
        g.fillRect(col2, row1, 150, 32);
        g.fillRect(col1, row2, 150, 32);
        g.fillRect(col2, row2, 150, 32);
        g.fillRect(col1, row3, 150, 32);

        // filling the one the player has clicked on
        g.setColor(new Color(111, 166, 189));
        switch (keyLabelAccessed) {
            case 0 -> g.fillRect(col1, row1, 150, 32);
            case 1 -> g.fillRect(col2, row1, 150, 32);
            case 2 -> g.fillRect(col1, row2, 150, 32);
            case 3 -> g.fillRect(col2, row2, 150, 32);
            case 4 -> g.fillRect(col1, row3, 150, 32);
        }

        // writing the typ eof key on the label
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        g.setColor(Color.BLACK);
        g.drawString("Up Key: " + ResourceDirectory.keycodeToName(ResourceDirectory.UP_KEY), col1 + 4, row1 + 18);
        g.drawString("Left Key: " + ResourceDirectory.keycodeToName(ResourceDirectory.LEFT_KEY), col2 + 4, row1 + 18);
        g.drawString("Down Key: " + ResourceDirectory.keycodeToName(ResourceDirectory.DOWN_KEY), col1 + 4, row2 + 18);
        g.drawString("Right Key: " + ResourceDirectory.keycodeToName(ResourceDirectory.RIGHT_KEY), col2 + 4, row2 + 18);
        g.drawString("Collect Key: " + ResourceDirectory.keycodeToName(ResourceDirectory.COLLECT_KEY), col1 + 4, row3 + 18);
        requestFocus();
        requestFocusInWindow();
    }
}
