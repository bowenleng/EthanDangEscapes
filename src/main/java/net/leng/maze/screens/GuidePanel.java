package net.leng.maze.screens;

import net.leng.maze.util.ResourceDirectory;

import javax.swing.*;
import java.awt.*;

public class GuidePanel extends JPanel {
    GuidePanel(Screen frame) {
        setBackground(Color.BLACK);
        add(Screen.makeButton("Go Back", l -> frame.openScreen(Screen.START)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Screen.drawIdkImage(g, getWidth(), getHeight());

        int beforeSpace = Math.max((getWidth() - 900)/2, 0) + 10;

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        g.drawString("Premise & Gameplay:", beforeSpace, 50);
        g.drawString("You play as a guy named Ethan Dang who is currently stuck in a maze.", beforeSpace, 75);
        g.drawString("Your goal is to guide him out of the maze so he may be reunited with his blanket boyfriend.", beforeSpace, 100);
        g.drawString("You have a limited number of hearts", beforeSpace, 125);
        g.drawString("- 7 on Effortless mode", beforeSpace + 30, 150);
        g.drawString("- 6 on Easy mode", beforeSpace + 30, 175);
        g.drawString("- 5 on Medium mode", beforeSpace + 30, 200);
        g.drawString("- 4 on Hard mode", beforeSpace + 30, 225);
        g.drawString("- 3 on XTREME mode", beforeSpace + 30, 250);

        g.drawString("Collectibles:", beforeSpace, 300);
        g.drawImage(ResourceDirectory.WEBTOON, beforeSpace, 305, 25, 25, null);
        g.drawString("is a regular BL webtoon comic which gives the player 1 point.", beforeSpace + 25, 325);
        g.drawImage(ResourceDirectory.GOOD_WEBTOON, beforeSpace, 330, 25, 25, null);
        g.drawString("is a good quality BL webtoon comic which gives the player 5 points.", beforeSpace + 25, 350);
        g.drawImage(ResourceDirectory.CHEESE, beforeSpace, 355, 25, 25, null);
        g.drawString("is cheese. Ethan hates cheese with a burning passion. Do NOT collect them, he will puke and lose hearts.", beforeSpace + 25, 375);

        g.drawString("Available Keys:", beforeSpace, 425);
        g.drawString("Movement is done via the following keys: "
                + ResourceDirectory.keycodeToName(ResourceDirectory.UP_KEY) + " for up, "
                + ResourceDirectory.keycodeToName(ResourceDirectory.LEFT_KEY) + " for left, "
                + ResourceDirectory.keycodeToName(ResourceDirectory.DOWN_KEY) + " for down, and "
                + ResourceDirectory.keycodeToName(ResourceDirectory.RIGHT_KEY) + " for right.", beforeSpace, 450);
        g.drawString("To enable collection or disable it, press " + ResourceDirectory.COLLECT_KEY + ".", beforeSpace, 475);
        g.drawString("Note that anytime you turn off collection mode, you consume points in the medium or harder difficulties.", beforeSpace, 500);

        g.drawString("Maze Types:", beforeSpace, 550);
        g.drawString("Easy Mazes to Solve: DFS and Binary Tree", beforeSpace, 575);
        g.drawString("Hard Mazes to Solve: Prim's and Kruskal's", beforeSpace, 600);

        g.drawString("Miscellaneous:", beforeSpace, 650);
        g.drawString("In effortless mode, there is a solver in the game which automatically solves the maze for you!", beforeSpace, 675);

        g.drawString("Author:", beforeSpace, 725);
        g.drawString("Code written, art designed, and premise written by Bowen Leng.", beforeSpace, 750);
        g.drawString("To Ethan on his birthday!", beforeSpace, 775);
    }
}
