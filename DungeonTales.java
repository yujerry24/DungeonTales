import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

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
	static Image pause;
	static Image menuBack;

	static class Level extends JPanel {

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
			}

			for (Rectangle r : getPlatforms()) {
				g.fillRect((int) r.getBounds().getMinX(), (int) r.getBounds()
						.getMinY(), (int) r.getWidth(), (int) r.getHeight());
			}

		}

		public Level(int level, int spawnX, int spawnY, int endX, int endY,
				Player p, Rectangle[] platforms) {
		//	setLayout(new BorderLayout());
			this.level = level;
			this.spawnX = spawnX;
			this.spawnY = spawnY;
			this.endX = endX;
			this.endY = endY;
			this.p = p;
			p.setX(spawnX);
			p.setY(spawnY);
			p.setVisible(true);
			this.platforms = platforms;
			this.isCompleted = false;
			LevelManager.levels[level - 1] = this;

			ActionListener al = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					repaint();
				}
			};

			Timer timer = new Timer(10, al);
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

		public Rectangle[] getPlatforms() {
			return this.platforms;
		}

	}

	static class LevelManager {
		public static Level[] levels = new Level[3];

		public static Level getLevel(int level) {
			for (Level l : levels) {
				if (l == null) {
					return null;
				}
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
		
		try {
			playMusicFile("MenuMusic.wav", true);
			door = ImageIO.read(new File("Door.png"));
			knight = ImageIO.read(new File("Knight.png"));
			menuBack = ImageIO.read(new File("menuBack.jpg"));
		} catch (IOException e) {
		} catch (LineUnavailableException e) {
		} catch (UnsupportedAudioFileException e) {
		}

		// Set the menu pane.
		menu = new MainMenu();
		pausePanel = new PausePanel();
		add(menu);

		
		addKeyListener(kl);
		this.requestFocusInWindow();
		this.setFocusable(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	static Clip clip;
	static MainMenu menu;

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
	
	static KeyListener kl = new KeyListener() {

		public void keyTyped(KeyEvent arg0) {
		}

		public void keyReleased(KeyEvent arg0) {
		}

		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
		
			if(key == KeyEvent.VK_ESCAPE){
				tales.setContentPane(pausePanel);
				tales.validate();
			}
			
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
	
	static PausePanel pausePanel;

	// Pause panel
	class PausePanel extends JPanel {
		
		public PausePanel(){
			setForeground(new Color(0, 0, 64));
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
		protected void paintComponent(Graphics g) {
			
			g.drawImage(menuBack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
			
		};
		
		public MainMenu() {
			final Level tutorial = LevelManager.getLevel(0);
			final Level one = LevelManager.getLevel(1);
			final Level two = LevelManager.getLevel(2);
			final Level three = LevelManager.getLevel(3);

			addKeyListener(kl);
			
			ActionListener al = new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					repaint();
					JButton button = (JButton) event.getSource();
					if (button == buttonT) {
						if (tutorial == null) {
							System.out.println("[ERROR] Unable to load level.");
							return;
						}
						try {
							stopMusicFile();
							playMusicFile("NonBoss.wav", true);
						} catch (LineUnavailableException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						tales.setContentPane(tutorial);
						tales.validate();
					} else if (button == back) {
						tales.setContentPane(panel);
						tales.validate();
					} else if (button == credits) {
						tales.setContentPane(panel5);
						tales.validate();
					} else if (button == button2) {
						if (one == null) {
							JOptionPane.showMessageDialog(panel,
									"Unable to load level!");
							System.out.println("[ERROR] Unable to load level.");
							return;
						}
						one.addKeyListener(kl);
						tales.remove(menu);
						tales.setContentPane(one);
						tales.validate();
						try {
							stopMusicFile();
							playMusicFile("NonBoss.wav", true);
						} catch (LineUnavailableException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (button == button3) {
						if (two == null) {
							JOptionPane.showMessageDialog(panel,
									"Unable to load level!");
							System.out.println("[ERROR] Unable to load level.");
							return;
						}
						two.addKeyListener(kl);
						tales.setContentPane(two);
						tales.validate();
						try {
							stopMusicFile();
							playMusicFile("NonBoss.wav", true);
						} catch (LineUnavailableException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						if (three == null) {
							JOptionPane.showMessageDialog(panel,
									"Unable to load level!");
							System.out.println("[ERROR] Unable to load level.");
							return;
						}
						three.addKeyListener(kl);
						tales.setContentPane(three);
						tales.validate();
						try {
							stopMusicFile();
							playMusicFile("NonBoss.wav", true);
						} catch (LineUnavailableException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedAudioFileException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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

			ImageIcon logo = new ImageIcon("Logo.png");
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
			p = new Player("John");
		}

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

		// Register levels
		registerLevels();

		// Activate the constructor, and create the frame.
		tales = new DungeonTales();

	}

	static DungeonTales tales;

	public static void registerLevels() {
		Rectangle[] onePlats = { new Rectangle(10, SCREEN_HEIGHT / 2, 500, 30) };
		Level one = new Level(1, 700, SCREEN_HEIGHT - GROUND_WIDTH - 150, 10,
				SCREEN_HEIGHT - GROUND_WIDTH - 150, p, onePlats);
		one.addKeyListener(kl);
	}

}
