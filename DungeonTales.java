import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;


public class DungeonTales extends JFrame {
	
	public DungeonTales(){
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(dim.width/2 + 200, dim.height/2 + 200);
		setLocation(dim.width/2 - getWidth()/2, dim.height/2 - getHeight()/2);
		setTitle("| Dungeon Tales |");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DungeonTales();
	}

}
