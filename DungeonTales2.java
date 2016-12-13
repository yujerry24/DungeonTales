
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

    static class Player  {
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

        public boolean isVisible(){
            return this.isVisible;
        }

        public void setVisible(boolean visible){
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(93, 100, 112));
            g.fillRect(0, SCREEN_HEIGHT - GROUND_WIDTH, SCREEN_WIDTH, SCREEN_HEIGHT);
            g.drawImage(door, getEndX(), getEndY(), 187, 187, null);
            if(p.isVisible()) {
                g.drawImage(knight, p.getX(), p.getY(), 150, 125, null);
            }else{

            }

        }

        public Level(int level, int spawnX, int spawnY, int endX, int endY, Player p) {
            this.level = level;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.endX = endX;
            this.endY = endY;
            this.p = p;
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

                if(p.getX() > SCREEN_WIDTH - 140){
                    p.setX(p.getX() - 5);
                }
                if(p.getX() < 10){
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

        Level one = LevelManager.getLevel(1);

        p.setVisible(true);

        setContentPane(one);

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

        // The hint or category is equal to the rest of the string from the colon.
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
        new DungeonTales();

    }

    public static void registerLevels() {
        Level one = new Level(1, 0 + 10, SCREEN_HEIGHT - GROUND_WIDTH - 100, SCREEN_WIDTH - 187, SCREEN_HEIGHT - GROUND_WIDTH - 175, p);
        p.setCurrentLevel(one);
        // TODO Set players location to the spawn point if loading for the first time.
    }

}
