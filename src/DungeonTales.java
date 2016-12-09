/*
DungeonTales.java
Justin, Alex, and Jerry
12/7/2016
Fun 2D platform game
 */

import javax.swing.JFrame;

public class DungeonTales extends JFrame {

    public DungeonTales(){
        setExtendedState(MAXIMIZED_BOTH);
        setTitle("| Dungeon Tales |");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    public static void main(String[] args) {
        // Activate the constructor, and create the frame.
        new DungeonTales();
    }

}
