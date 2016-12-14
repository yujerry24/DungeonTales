import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class DungeonTales extends JFrame {

	static Player p;

	static class Player {
		private int x;
		private int y;
		private String name;
		private Level currentLevel;
		private boolean isVisible;

		public Player(String name) {
			x = 0;
			y = 0;
			this.name = name;
			this.isVisible = false;

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

		public void setCurrentLevel(Level currentLevel) {
			this.currentLevel = currentLevel;
		}

		public boolean isVisible() {
			return this.isVisible;
		}

		public void setVisible(boolean visible) {
			this.isVisible = visible;
		}

	}

	final static int GROUND_WIDTH = 100;

	static Image door;
	static Image knight;

	static class Level extends JLayeredPane {

		private int level;
		private int spawnX, spawnY, endX, endY;
		private boolean isCompleted;
		private Player p;
		private Rectangle[] platforms;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(new Color(93, 100, 112));
			g.fillRect(0, SCREEN_HEIGHT - GROUND_WIDTH, SCREEN_WIDTH,
					SCREEN_HEIGHT);
			g.drawImage(door, getEndX(), getEndY(), 187, 187, null);
			if (p.isVisible()) {
				g.drawImage(knight, p.getX(), p.getY(), 150, 125, null);
			} else {

			}

		}

		public Level(int level, int spawnX, int spawnY, int endX, int endY,
				Player p, Rectangle[] platforms) {
			this.level = level;
			this.spawnX = spawnX;
			this.spawnY = spawnY;
			this.endX = endX;
			this.endY = endY;
			this.p = p;
			this.platforms = platforms;
			this.isCompleted = false;
			LevelManager.levels[level - 1] = this;

			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					repaint();
				}
			};

			if (p == null) {
				return;
			}

			Timer timer = new Timer(20, al);
			timer.start();
		}

		public int getSpawnX() {
			return this.spawnX;
		}

		public int getSpawnY() {
			return this.spawnY;
		}

		public int getEndX() {
			return this.endX;
		}

		public int getEndY() {
			return this.endY;
		}

		public int getLevel() {
			return this.level;
		}

		public boolean isCompleted() {
			return this.isCompleted;
		}

	}

	static class LevelManager {
		public static Level[] levels = new Level[1];

		public static Level getLevel(int level) {

			for (Level l : levels) {
				if (l.getLevel() == level) {
					return l;
				}
			}

			return null;
		}
	}

	static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

	final static int SCREEN_HEIGHT = (int) dim.getHeight();
	final static int SCREEN_WIDTH = (int) dim.getWidth();

	public DungeonTales() {
		setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		setResizable(false);
		setTitle("| Dungeon Tales |");

		KeyListener kl = new KeyListener() {

			public void keyTyped(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
			}

			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();

				if (p.getX() > SCREEN_WIDTH - 140) {
					p.setX(p.getX() - 5);
				}
				if (p.getX() < 10) {
					p.setX(p.getX() + 5);
				}

				if (key == KeyEvent.VK_RIGHT) {
					p.setX(p.getX() + 5);
				}
				if (key == KeyEvent.VK_LEFT) {
					p.setX(p.getX() - 5);
				}

			}
		};

		addKeyListener(kl);

		try {
			playMusicFile("MenuMusic.wav", true);
			door = ImageIO.read(new File("Door.png"));
			knight = ImageIO.read(new File("Knight.png"));
		} catch (IOException e) {
		} catch (LineUnavailableException e) {
		} catch (UnsupportedAudioFileException e) {
		}

		p.setVisible(true);

		// Set the menu pane.
		MainMenu menu = new MainMenu();

		add(menu);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	static Clip clip;

	public static void playMusicFile(String file, boolean loop)
			throws IOException, LineUnavailableException,
			UnsupportedAudioFileException {
		File sound = new File(file);

		if (!sound.exists()) {
			System.out.println("[ERROR] Sound file not found.");
			return;
		}

		AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
		clip = AudioSystem.getClip();
		clip.open(stream);
		if (loop) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			clip.loop(1);
		}

	}

	public static void stopMusicFile() throws LineUnavailableException {
		if (clip == null) {
			return;
		}
		clip.stop();
		clip.close();
	}

	public static String getFileValue(String path) {
		int index = path.indexOf(":");

		String value = "";

		if (index == -1) {
			// Hint is not found.
			System.out.println("[ERROR] No category found!");
			return null;
		}

		// The hint or category is equal to the rest of the string from the
		// colon.
		value = path.substring(index + 1);

		value.trim();

		return value;
	}

	public static boolean hasData() {
		if (p == null) {
			return false;
		} else {
			return true;
		}
	}

	// Main menu panel
	class MainMenu extends JPanel {

		JButton buttonT = new JButton("Tutorial");
		JButton button2 = new JButton("Level 1");
		JButton button3 = new JButton("Level 2");
		JButton button4 = new JButton("Level 3");
		JButton credits = new JButton("Credits");
		JButton back = new JButton("QUIT");
		JPanel panel1 = new JPanel(), panel2 = new JPanel(),
				panel3 = new JPanel(), panel4 = new JPanel(),
				panel5 = new JPanel();

		JPanel panel = this;

		public MainMenu() {
			ActionListener al = new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					JButton button = (JButton) event.getSource();
					if (button == buttonT) {
						setContentPane(panel1);
						tales.validate();
					} else if (button == back) {
						tales.setContentPane(panel);
						tales.validate();
					} else if (button == credits) {
						tales.setContentPane(panel5);
						tales.validate();
					} else if (button == button2) {
						setContentPane(panel2);
						tales.validate();
					} else if (button == button3) {
						setContentPane(panel3);
						tales.validate();
					} else {
						setContentPane(panel4);
						tales.validate();
					}
				}
			};

			FlowLayout flow = new FlowLayout();
			this.setLayout(flow);

			Dimension dimB = new Dimension(200, 60);

			back.setLocation(100, 100);
			back.setPreferredSize(dimB);
			panel1.add(back);
			panel2.add(back);
			// panel3.add(back);
			// panel4.add(back);
			// panel5.add(back);
			back.addActionListener(al);

			buttonT.setLocation(100, 100);
			buttonT.setPreferredSize(dimB);
			add(buttonT);
			buttonT.addActionListener(al);

			button2.setLocation(100, 100);
			button2.setPreferredSize(dimB);
			add(button2);
			button2.addActionListener(al);

			button3.setLocation(100, 100);
			button3.setPreferredSize(dimB);
			add(button3);
			button3.addActionListener(al);

			button4.setLocation(100, 100);
			button4.setPreferredSize(dimB);
			add(button4);
			button4.addActionListener(al);

			credits.setLocation(100, 100);
			credits.setPreferredSize(dimB);
			add(credits);
			credits.addActionListener(al);

			panel1.setBackground(new Color(100, 100, 100));

			ImageIcon logo = new ImageIcon("logo.fw.png");
			JLabel label3 = new JLabel(logo);
			label3.setPreferredSize(new Dimension(1000, 800));
			label3.setLocation(500, 500);
			add(label3);

		}

	}

	public static void main(String[] args) throws IOException {

		// Create a save file if it does not already exist.
		File save = new File("save.txt");
		if (!save.exists()) {
			save.createNewFile();
			PrintWriter out = new PrintWriter(save);
			out.println("- DUNGEON TALES SAVE FILE -");
			out.close();
		} else {
			Scanner input = new Scanner(save);
			while (input.hasNext()) {
				String line = input.nextLine();
				if (line.indexOf("Player Name:") != -1) {
					String name = getFileValue(line);
					p = new Player(name);
				} else {
					p = new Player("John");
				}
			}
			input.close();
		}

		// Register levels
		registerLevels();

		// Activate the constructor, and create the frame.
		tales = new DungeonTales();

	}

	static DungeonTales tales;

	public static void registerLevels() {
		// TODO Set players location to the spawn point if loading for the first
		// time.
	}

}
