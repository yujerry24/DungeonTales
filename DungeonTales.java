import javax.swing.JFrame;

public class DungeonTales extends JFrame {

    public DungeonTales(){
        setExtendedState(MAXIMIZED_BOTH);
        setTitle("| Dungeon Tales |");
        
        // Hey jordan, how are you?

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    public static void main(String[] args) {
        // Activate the constructor, and create the frame.
        new DungeonTales();
    }

}
