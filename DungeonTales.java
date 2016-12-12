import javax.swing.JFrame;

public class DungeonTales extends JFrame {

	class Player {
		private int x;
		private int y;
		private String name;
		
		public Player(String name){
			x = 0;
			y = 0;
			this.name = name;;
		}
		
		public String getName(){
			return name;
		}
		
		public int getX(){
			return this.x;
		}
		
		public int getY(){
			return this.y;
		}
		
		public void setX(int x){
			this.x = x;
		}
		
		public void setY(int y){
			this.y = y;
		}
		
	}
	
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
