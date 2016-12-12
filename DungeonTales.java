
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class DungeonTales extends JFrame {

	static Player p;
	
	static class Player {
		private int x;
		private int y;
		private String name;

		public Player(String name) {
			x = 0;
			y = 0;
			this.name = name;
			;
		}

		public String getName() {
			return name;
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

	}
	
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	
	final int SCREEN_HEIGHT = (int) dim.getHeight();
	final int SCREEN_WIDTH = (int) dim.getWidth();

	public DungeonTales() {
		setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		setResizable(false);
		setTitle("| Dungeon Tales |");
		
		try {
			playMusicFile("MenuMusic.wav", true);
		} catch (IOException e) {
		} catch (LineUnavailableException e) {
		} catch (UnsupportedAudioFileException e) {
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	static Clip clip;

	public static void playMusicFile(String file, boolean loop)
			throws IOException, LineUnavailableException,
			UnsupportedAudioFileException {
		File sound = new File(file);

		AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
		clip = AudioSystem.getClip();
		clip.open(stream);
		if (loop) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			clip.loop(1);
		}

	}
	
	public static void stopMusicFile() throws LineUnavailableException{
		if(clip == null){
			return;
		}
		clip.stop();
		clip.close();
	}
	
	public static String getFileValue(String path){
		int index = path.indexOf(":");
        
		String value = "";
		
        if(index == -1){
          // Hint is not found.
          System.out.println("[ERROR] No category found!");
          return null;
        }
               
        // The hint or category is equal to the rest of the string from the colon.
        value = path.substring(index + 1);
        
        value.trim();
        
        return value;
	}

	public static boolean hasData(){
		if(p == null){
			return false;
		}else{
			return true;
		}
	}
	
	public static void main(String[] args) throws IOException {	
		
		// Create a save file if it does not already exist.
		File save = new File("save.txt");
		if(!save.exists()){
			save.createNewFile();
			PrintWriter out = new PrintWriter(save);
			out.println("- DUNGEON TALES SAVE FILE -");
			out.close();
		}else{
			Scanner input = new Scanner(save);
			while(input.hasNext()){
				String line = input.nextLine();
				if(line.indexOf("Player Name:") != -1){
					String name = getFileValue(line);
					p = new Player(name);
				}
			}
			input.close();
		}
		
		// Activate the constructor, and create the frame.
		new DungeonTales();
	}

}
