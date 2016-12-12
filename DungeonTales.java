import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class DungeonTales extends JFrame {

	class Player {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void playMusicFile(String file, boolean loop)
			throws IOException, LineUnavailableException,
			UnsupportedAudioFileException {
		File sound = new File(file);

		AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
		Clip clip = AudioSystem.getClip();
		clip.open(stream);
		if (loop) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			clip.loop(1);
		}

	}

	public static void main(String[] args) {
		// Activate the constructor, and create the frame.
		new DungeonTales();
	}

}
