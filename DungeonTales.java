import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
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

    // [Boolean variables]
    static boolean doGravity = true;
    // [-----------------]

    // [Timer variables]
    static Timer movement;
    static Timer jump;
    // [---------------]

    // [Image variables]
    static Image door;
    static Image knight;
    static Image knight2;
    static Image knight3;
    static Image pause;
    static Image menuBack;
    static Image spikeImage;
    static Image platformImage;
    // [---------------]

    // [Other variables]
    static Player p;
    static PausePanel pausePanel;
    static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    static Clip clip;
    static MainMenu menu;
    static DungeonTales tales;
    // Create an array of pressed keys.
    static int[] pressed = new int[1];
    // [---------------]

    // [Integer variables]
    static int count = 0;
    final static int GROUND_WIDTH = 152;
    final static int SCREEN_HEIGHT = (int) dim.getHeight();
    final static int SCREEN_WIDTH = (int) dim.getWidth();
    static final int PLAYER_WIDTH = 150;
    static final int PLAYER_HEIGHT = 125;
    // [-----------------]


    // Player object class.
    static class Player {
        // A set of variables that belong to the player.
        private int x;
        private int y;
        private String name;
        private Level currentLevel;
        private boolean isVisible;
        private boolean isPaused;
        private boolean canPause;
        private boolean canMove = true;
        private boolean isMoving = false;
        private boolean isJumping;
        private boolean isFalling;
        private boolean onPlat = false;

        // Constructor for player.
        public Player(String name) {
            // Initialize the variables.
            x = 0;
            y = 0;
            this.name = name;
            this.isVisible = false;
            this.isPaused = false;
            this.canPause = false;
            this.isJumping = false;
            this.isFalling = false;
        }

        // Method to return the player name.
        public String getName() {
            return name;
        }

        // Method to return the players X location.
        public int getX() {
            return this.x;
        }

        // Method to return the players Y location.
        public int getY() {
            return this.y;
        }

        // Method to return if the player can move.
        public boolean canMove() {
            return this.canMove;
        }

        // Method to check if the game is paused.
        public boolean isPaused() {
            return this.isPaused;
        }

        // Method to get the current level.
        public Level getCurrentLevel() {
            return this.currentLevel;
        }

        // Method to set the X location of the player.
        public void setX(int x) {
            this.x = x;
        }

        // Method to set if the player can move.
        public void setCanMove(boolean canMove) {
            this.canMove = canMove;
        }

        // Method to check if the player is falling.
        public boolean isFalling() {
            return this.isFalling;
        }

        // Method to set if the player is falling.
        public void setFalling(boolean yes) {
            this.isFalling = yes;
        }

        // Method to set the Y location of the player.
        public void setY(int y) {
            this.y = y;
        }

        // Method to set the current level of the player.
        public void setCurrentLevel(Level currentLevel) {
            this.currentLevel = currentLevel;
        }

        // Method to check if the player is visible.
        public boolean isVisible() {
            return this.isVisible;
        }

        // Method to set the player visible or not.
        public void setVisible(boolean visible) {
            this.isVisible = visible;
        }

        // Method to pause or unpause the game.
        public void setPaused(boolean pause) {
            this.isPaused = pause;
        }

    }

    static class Platform {
        private int startX = 0;
        private int startY = 0;
        private int endX;
        private int endY;
        private int x;
        private int y;
        private int width;
        private int height;
        private Level level;
        private int id;
        private int speed;
        private boolean back;
        private boolean hasPlayer = false;

        public Platform(int startX, int startY, int endX, int endY, int width,
                        int height, Level level, int id, int speed) {
            this.startX = startX;
            this.startY = startY;
            this.speed = speed;
            this.x = startX;
            this.y = startY;
            this.endX = endX;
            this.endY = endY;
            this.width = width;
            this.height = height;
            this.level = level;
            this.back = false;
            this.level.addPlatform(id, this);
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getSpeed() {
            return this.speed;
        }

        public boolean hasPlayer() {
            return this.hasPlayer;
        }

        public void hasPlayer(boolean yes) {
            this.hasPlayer = yes;
        }

        public boolean getBack() {
            return this.back;
        }

        public void setBack(boolean back) {
            this.back = back;
        }

    }

    static class GameTimer {
        private Timer timer;
        private int time;
        private ActionListener al;
        private Level level;

        public GameTimer(Level level) {
            this.level = level;
            this.time = 0;
            al = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    time++;
                }
            };
            this.timer = new Timer(1000, al);
        }

        public void pauseTime() {
            this.timer.stop();
        }

        public void resumeTime() {
            this.timer.start();
        }

        public int getTime() {
            return this.time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public void resetTime() {
            this.time = 0;
        }

    }

    static class Level extends JPanel {

        private int level;
        private int spawnX, spawnY, endX, endY;
        private Player p;
        private Rectangle[] platforms;
        private Platform[] movingPlats;
        private Rectangle[] spikes;
        private GameTimer gt;
        private JLabel time;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(platformImage, 0, SCREEN_HEIGHT - GROUND_WIDTH, SCREEN_WIDTH, GROUND_WIDTH, null);
            g.setColor(new Color(93, 100, 112));
            g.fillRect(0, SCREEN_HEIGHT - GROUND_WIDTH, SCREEN_WIDTH,
                    GROUND_WIDTH);
            g.drawImage(menuBack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
            g.drawImage(door, endX, endY, 187, 187, null);

            for (Rectangle r : getPlatforms()) {

                if (r == null) {
                    return;
                }

                g.drawImage(platformImage, (int) r.getBounds().getMinX(),
                        (int) r.getBounds().getMinY(), (int) r.getWidth(),
                        (int) r.getHeight(), null);
            }

            for (Rectangle s : getSpikes()) {
                g.drawImage(spikeImage, (int) s.getBounds().getMinX(), (int) s
                        .getBounds().getMinY(), (int) s.getWidth(), (int) s
                        .getHeight(), null);
            }

            for (Platform p : getMovingPlats()) {

                if (p == null) {
                    return;
                }

                //g.fillRect(p.getX(), p.getY(), p.width, p.height);
                g.drawImage(platformImage, (int) p.getX(), (int) p.getY(), p.width, p.height, null);
            }

            if (p.isVisible()) {
                g.drawImage(knight, p.getX(), p.getY(), 150, 125, null);
            }

        }

        public Level(int level, int spawnX, int spawnY, int endX, int endY,
                     final Player p, Rectangle[] platforms, int movingPlats,
                     Rectangle[] spikes) {
            this.level = level;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.endX = endX;
            this.endY = endY;
            this.p = p;
            this.spikes = spikes;
            gt = new GameTimer(this);

            p.setX(spawnX);
            p.setY(spawnY);
            p.setVisible(true);
            this.platforms = platforms;
            this.movingPlats = new Platform[movingPlats];
            LevelManager.levels[level - 1] = this;

            setLayout(null);

            time = new JLabel("<html><b>Time: " + this.getGameTimer().getTime()
                    + "s</b></html>");
            time.setBounds(SCREEN_WIDTH - 150, 0, 200, 100);

            add(time);

            if (level == 4) {
                setLayout(null);
                // Create the tutorial messages
                JLabel moveTip = new JLabel(
                        "<html><b>TIP:</b><br>Use the arrow keys<br>to navigate the level!</html>");
                moveTip.setBounds(getSpawnX() + 20, getSpawnY() - 160, 300, 100);
                moveTip.setForeground(Color.white);
                moveTip.setFont(new Font(moveTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(moveTip);

                JLabel doorTip = new JLabel(
                        "<html><b>TIP:</b><br>Reach these doors<br>to complete the level!</html>");
                doorTip.setBounds(getEndX() + 20, getSpawnY() - 180, 300, 100);
                doorTip.setForeground(Color.white);
                doorTip.setFont(new Font(doorTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(doorTip);

                JLabel platTip = new JLabel(
                        "<html><b>TIP:</b><br>Jump on these platforms<br>to reach higher parts!</html>");
                platTip.setBounds(SCREEN_WIDTH / 2 - 570, getSpawnY() - 200,
                        300, 100);
                platTip.setForeground(Color.white);
                platTip.setFont(new Font(platTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(platTip);

                JLabel spikeTip = new JLabel(
                        "<html><b>TIP:</b><br>Avoid touching these<br>spikes or you'll respawn!</html>");
                spikeTip.setBounds(SCREEN_WIDTH / 2 - 270, getSpawnY() - 400,
                        300, 100);
                spikeTip.setForeground(Color.white);
                spikeTip.setFont(new Font(spikeTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(spikeTip);

                JLabel movingPlatTip = new JLabel(
                        "<html><b>TIP:</b><br>These platforms move, side to<br>side, and up and down.<br>use them to reach platforms!</html>");
                movingPlatTip.setBounds(SCREEN_WIDTH / 2, getSpawnY() - 300,
                        300, 100);
                movingPlatTip.setForeground(Color.white);
                movingPlatTip.setFont(new Font(movingPlatTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(movingPlatTip);
            }

            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {

                    time.setText("<html><b>Time: " + getGameTimer().getTime()
                            + "s</b></html>");
                    time.setForeground(Color.WHITE);
                    time.setFont(new Font(time.getFont().getName(), Font.PLAIN,
                            27));

                    repaint();
                    if (p.isPaused()) {
                        return;
                    }
                    for (Platform p : getMovingPlats()) {

                        if (p == null) {
                            return;
                        }

                        Player pl = DungeonTales.p;

                        if (p.startX != p.endX) {
                            if (p.getX() >= p.endX - p.width || p.getBack()) {
                                p.setX(p.getX() - p.getSpeed());
                                p.setBack(true);
                                if (p.getX() == p.startX) {
                                    p.setBack(false);
                                }
                            } else {
                                p.setX(p.getX() + p.getSpeed());
                            }
                        }

                        if (p.startY != p.endY) {
                            if (p.getY() >= p.endY - p.width || p.getBack()) {
                                p.setY(p.getY() - p.getSpeed());
                                p.setBack(true);
                                if (p.getY() == p.startY) {
                                    p.setBack(false);
                                    if (p.hasPlayer()) {
                                        pl.setY(pl.getY() - 5);
                                    }
                                }
                            } else {
                                p.setY(p.getY() + p.getSpeed());
                            }
                        }

                        if (p.hasPlayer()) {
                            if (p.startX != p.endX) {
                                if (p.getBack()) {
                                    pl.setX(pl.getX() - p.getSpeed());
                                } else {
                                    pl.setX(pl.getX() + p.getSpeed());
                                }
                            }
                            if (p.startY != p.endY) {
                                if (p.getBack()) {
                                    pl.setY(pl.getY() - p.getSpeed());
                                } else {
                                    pl.setY(pl.getY() + p.getSpeed());
                                }
                            }
                        }
                    }
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

            File save = new File("save.txt");

            // Create a scanner for the file.
            Scanner input = null;
            try {
                input = new Scanner(save);
            } catch (IOException e) {
                System.out.println("[ERROR] Unable to access save.txt");
            }

            if (input == null) {
                return false;
            }

            // Loop through each file value.
            while (input.hasNext()) {
                // Get the line in the file.
                String line = input.nextLine();
                // Get the saved player name from the file.
                if (line.equalsIgnoreCase(Integer.toString(getLevel()) + "Completed:true")) {
                    // Get the value
                    String completed = getFileValue(line);
                    if (completed.equalsIgnoreCase("true")) {
                        input.close();
                        return true;
                    }
                }
            }
            // Close the input.
            input.close();

            return false;
        }

        public void setCompleted(boolean completed) {
            try {
                writeToFile(getLevel() + "Completed:true");
            } catch (IOException e) {
            }
        }

        public Platform[] getMovingPlats() {
            return this.movingPlats;
        }

        public Rectangle[] getPlatforms() {
            return this.platforms;
        }

        public Rectangle[] getSpikes() {
            return this.spikes;
        }

        public void addPlatform(int id, Platform form) {
            this.movingPlats[id - 1] = form;
        }

        public GameTimer getGameTimer() {
            return this.gt;
        }

    }

    static class LevelManager {
        public static Level[] levels = new Level[4];

        public static Level getLevel(int level) {
            for (Level l : levels) {
                if (l == null) {
                    continue;
                }
                if (l.getLevel() == level) {
                    return l;
                }
            }
            return null;
        }
    }

    public DungeonTales() {
        setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setResizable(false);
        setTitle("| Dungeon Tales |");

        try {
            playMusicFile("MenuMusic.wav", true);
            door = ImageIO.read(new File("Door.png"));
            knight = ImageIO.read(new File("Knight.png"));
            knight2 = ImageIO.read(new File("Knight2.png"));
            knight3 = ImageIO.read(new File("Knight.png"));
            menuBack = ImageIO.read(new File("menuBack.jpg"));
            spikeImage = ImageIO.read(new File("spikes.png"));
            platformImage = ImageIO.read(new File("platformWall.jpg"));
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

        movement = new Timer(8, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (!(p.canMove())) {
                    return;
                }

                if (p.isPaused()) {
                    return;
                }

                if (pressed[0] == 0) {
                    return;
                }

                int key = pressed[0];

                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT) {
                    if (p.getX() > SCREEN_WIDTH - 140) {
                        p.setX(p.getX() - 4);
                    }
                    if (p.getX() < 10) {
                        p.setX(p.getX() + 4);
                    }
                }

                if (p.getCurrentLevel() == null) {
                    return;
                }

                Rectangle door = new Rectangle(
                        p.getCurrentLevel().getEndX() + 90, p.getCurrentLevel()
                        .getEndY(), 187 - 100, 187);
                Rectangle middlePlayer = new Rectangle(p.getX() + PLAYER_WIDTH
                        / 2, p.getY(), 1, 1);

                if (middlePlayer.intersects(door)) {
                    p.getCurrentLevel().getGameTimer().pauseTime();
                    if (p.getCurrentLevel().getLevel() == 4) {
                        JOptionPane.showMessageDialog(p.getCurrentLevel(),
                                "You've completed the tutorial in " + p.getCurrentLevel().getGameTimer().getTime() + " seconds!");
                    } else {
                        JOptionPane.showMessageDialog(p.getCurrentLevel(),
                                "You've completed level "
                                        + p.getCurrentLevel().getLevel() + " in " + p.getCurrentLevel().getGameTimer().getTime() + " seconds!");
                    }
                    p.setCanMove(false);
                    tales.remove(p.getCurrentLevel());
                    p.getCurrentLevel().getGameTimer().resetTime();
                    tales.setContentPane(new MainMenu());
                    tales.validate();
                    pressed[0] = 0;
                    p.getCurrentLevel().setCompleted(true);
                    try {
                        stopMusicFile();
                        playMusicFile("MenuMusic.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                }

                if (pressed[0] == KeyEvent.VK_LEFT) {
                    p.setX(p.getX() - 4);
                } else if (pressed[0] == KeyEvent.VK_RIGHT) {
                    p.setX(p.getX() + 4);
                }

            }
        });

        Timer collide = new Timer(5, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a rectangle at the player. (for the right)
                Rectangle rightPlayer = new Rectangle(p.getX() + 90 / 2, p
                        .getY(), PLAYER_WIDTH - 80, PLAYER_HEIGHT - 20);
                // p.getCurrentLevel().getGraphics().drawRect(p.getX() + 80/2,
                // p.getY(), PLAYER_WIDTH - 70, PLAYER_HEIGHT - 20);

                // Create a rectangle at the player. (for the left)
                Rectangle leftPlayer = new Rectangle(p.getX() + 40, p.getY(),
                        PLAYER_WIDTH / 2 - 30, PLAYER_HEIGHT - 20);

                // Create a rectangle at the player.
                Rectangle player = new Rectangle(p.getX() + PLAYER_WIDTH / 2
                        - 20, p.getY() + PLAYER_HEIGHT - 5,
                        PLAYER_WIDTH / 2 - 30, 1);

                if (p.getCurrentLevel() == null) {
                    return;
                }

                for (Rectangle r : p.getCurrentLevel().getPlatforms()) {
                    if (r.intersects(rightPlayer)) {
                        p.setX(p.getX() - 4);
                        return;
                    } else if (r.intersects(leftPlayer)) {
                        p.setX(p.getX() + 4);
                        return;
                    }
                }

                for (Rectangle spike : p.getCurrentLevel().getSpikes()) {
                    Rectangle newSpike = new Rectangle((int) spike.getX() + 50,
                            (int) spike.getY(), spike.width - 95, spike.height);

                    if (newSpike.intersects(player)
                            || newSpike.intersects(leftPlayer)
                            || newSpike.intersects(rightPlayer)) {
                        // Player dies.
                        p.setX(p.getCurrentLevel().getSpawnX());
                        p.setY(p.getCurrentLevel().getSpawnY());
                    }
                }
            }
        });
        collide.start();

        // Create a new loop to run gravity.
        Timer gravity = new Timer(5, new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // If the boolean is true, allow gravity to be applied.
                if (doGravity) {

                    // Only apply gravity when the player is within a level
                    // which has loaded properly.
                    if (p.getCurrentLevel() == null) {
                        return;
                    }

                    if (p.isPaused) {
                        return;
                    }

                    // Create a rectangle at the player.
                    Rectangle player = new Rectangle(p.getX() + PLAYER_WIDTH
                            / 2 - 20, p.getY() + PLAYER_HEIGHT - 5,
                            PLAYER_WIDTH / 2 - 30, 1);

                    // Test code to draw the location of rectangle.
                    // p.getCurrentLevel().getGraphics().setColor(Color.red);
                    // p.getCurrentLevel().getGraphics().fillRect(p.getX() +
                    // PLAYER_WIDTH/2 - 20, p.getY() + PLAYER_HEIGHT - 5,
                    // PLAYER_WIDTH/2 - 30, 1);

                    // Loop through all the platforms on the level, and compare
                    // their locations with the player.
                    for (Rectangle plat : p.getCurrentLevel().getPlatforms()) {
                        Rectangle newPlat = new Rectangle((int) plat.getX(), (int) plat.getY(), plat.width, 8);
                        if (newPlat.intersects(player)) {

                            // Make sure the player is falling
                            if (!p.isFalling) {
                                return;
                            }

                            // Player is ontop of a platform, so gravity is not
                            // applied.
                            p.setFalling(false);
                            return;
                        }
                    }

                    // Moving platform collision
                    for (Platform plat : p.getCurrentLevel().getMovingPlats()) {

                        // Make sure that the platform is not null.
                        if (plat == null) {
                            return;
                        }

                        // Create a rectangle out of the platform object.
                        Rectangle r = new Rectangle(plat.getX(), plat.getY(),
                                plat.width, 8);

                        // Check if the players rectangle is intersecting with a
                        // platform.
                        if (r.intersects(player)) {
                            // If they are intersecting, then add the player to
                            // that platform.
                            // This is done for moving platforms to allow the
                            // player to stay on the platform as it moves.
                            plat.hasPlayer(true);
                            // Return to stop gravity from applying.
                            p.setFalling(false);
                            return;
                        } else {
                            // Else the player is no longer on a platform.
                            // Check to see if the platform that we were
                            // checking had the player on top of it.
                            if (plat.hasPlayer()) {
                                // The player was on this platform, so remove
                                // the player from that platform.
                                plat.hasPlayer(false);
                            }
                        }
                    }

                    // Main gravity control. Sends player to the lowest possible
                    // point on the level (Assuming no platform is found)
                    if (p.getY() < SCREEN_HEIGHT - GROUND_WIDTH) {
                        // Make the player fall.
                        p.setFalling(true);
                        p.setY(p.getY() + 6);
                    } else {
                        p.setFalling(false);
                    }
                }
            }
        });
        // Start the gravity timer.
        gravity.start();
    }

    /*
     * Method to play a music file.
     *
     * @param The file of music you would like to play.
     *
     * @param Boolean for whether or not you would like to loop it.
     *
     * @returns Nothing, a void method.
     */
    public static void playMusicFile(String file, boolean loop)
            throws IOException, LineUnavailableException,
            UnsupportedAudioFileException {
        // Create a new file using the filename parameter.
        File sound = new File(file);

        // Make sure the file exists.
        if (!sound.exists()) {
            System.out.println("[ERROR] Sound file not found.");
            return;
        }

        // Play the audio file.
        AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
        clip = AudioSystem.getClip();
        // Open the audio stream.
        clip.open(stream);
        // If loop boolean is true, then loop the sound file.
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.loop(1);
        }

    }

    /*
     * Method to stop any music file currently playing.
     */
    public static void stopMusicFile() throws LineUnavailableException {
        // If no music is being played, return.
        if (clip == null) {
            return;
        }
        // Stop the music, and close the clip.
        clip.stop();
        clip.close();
    }

    /*
     * Method used to easily obtain stored values from the save file.
     *
     * @param The string you would like to get the value of.
     *
     * @returns The value as a string.
     */
    public static String getFileValue(String path) {
        // Get the location of the value separator ":".
        int index = path.indexOf(":");

        String value = "";

        // If there isn't a separator, return null.
        if (index == -1) {
            // Hint is not found.
            System.out.println("[ERROR] No category found!");
            return null;
        }

        // Get the value using substring from the separator point.
        value = path.substring(index + 1);

        // Trim any excess spaces.
        value.trim();

        // Return the value.
        return value;
    }

    // Create a new key listener to listen for key events.
    static KeyListener kl = new KeyListener() {

        public void keyTyped(KeyEvent arg0) {

        }

        public void keyReleased(KeyEvent e) {
            // Get the key that was released.
            int key = e.getKeyCode();

            // if the player hasn't even moved, then return.
            if (movement == null) {
                return;
            }

            // If the key that is released was the left arrow remove it from the
            // array.
            if (key == KeyEvent.VK_LEFT) {
                pressed[0] = 0;
                // Stop the movement timer.
                movement.stop();
            } else if (key == KeyEvent.VK_RIGHT) {
                // Reset the array.
                pressed[0] = 0;
                // Stop the movement timer.
                movement.stop();
            }
        }

        public void keyPressed(KeyEvent e) {
            // Get the key that was pressed.
            int key = e.getKeyCode();

            // If the player pressed escape, pause the game.
            if (key == KeyEvent.VK_ESCAPE) {
                // Make sure that the player is on a level, so they cannot pause
                // at the menu screen.
                if (menu.isVisible() || !p.canPause) {
                    if (menu.isVisible()) {
                        for (Component c : menu.getComponents()) {
                            if (c instanceof JButton) {
                                JButton button = (JButton) c;
                                button.setForeground(Color.white);
                            }
                        }
                        tales.setContentPane(menu);
                    }
                    return;
                }

                // Check to see if the game is paused already.
                if (p.isPaused()) {
                    // The game is already paused, so remove the paused panel.
                    tales.remove(pausePanel);
                    // Set the main panel to the current level.
                    tales.setContentPane(p.getCurrentLevel());
                    // Reset the screen.
                    tales.validate();
                    // Set paused to false - resume all game functions (movement
                    // etc.)
                    p.setPaused(false);
                    // Resume the game timer
                    p.getCurrentLevel().getGameTimer().resumeTime();
                    // if the level is the tutorial level, reappear all the
                    // JLabels.
                    for (Component c : p.getCurrentLevel().getComponents()) {
                        c.setVisible(true);
                    }
                    // Set the tutorial layout to absolute for JLabel
                    // positioning.
                    p.getCurrentLevel().setLayout(null);
                    // Restart playing the in game music file.
                    try {
                        stopMusicFile();
                        playMusicFile("NonBoss.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    return;
                }
                // The game is not paused yet, so stop the music file.
                try {
                    stopMusicFile();
                } catch (LineUnavailableException ee) {
                }
                // Check if the level being paused is the tutorial.
                // Create a level variable.
                Level tutorial = p.getCurrentLevel();

                // Hide all the components on the level (JLabels).
                for (Component c : tutorial.getComponents()) {
                    c.setVisible(false);
                }
                // Add a layout to the screen for the pause menu.
                tutorial.setLayout(new FlowLayout());
                // Set paused to true.
                p.setPaused(true);
                // Pause the timer
                p.getCurrentLevel().getGameTimer().pauseTime();
                // Add the pause panel.
                tales.add(pausePanel);
                // Repaint the current level to apply the new changes.
                p.getCurrentLevel().repaint();
                // Refresh the pane.
                tales.validate();
            }

            // If the game is paused, then block all movement.
            if (p.isPaused()) {
                return;
            }

            // If the player is attempting to leave the screen send them back.
            if (p.getX() > SCREEN_WIDTH - 140) {
                p.setX(p.getX() - 7);
            }
            if (p.getX() < 10) {
                p.setX(p.getX() + 7);
            }

            // If the movement is null there was a problem so return.
            if (movement == null) {
                return;
            }

            // Check to see if no keys are being pressed.
            if (pressed[0] == 0) {
                // If the right arrow was pressed, add the key to the array.
                if (key == KeyEvent.VK_RIGHT) {
                    pressed[0] = KeyEvent.VK_RIGHT;
                    // Start the movement timer.
                    movement.start();
                    // Change the picture to move right.
                    knight = knight3;
                }
                // If the left key was pressed, add it to the array.
                if (key == KeyEvent.VK_LEFT) {
                    pressed[0] = KeyEvent.VK_LEFT;
                    // Start the movement timer.
                    movement.start();
                    // Change the image to the left.
                    knight = knight2;
                }
            }

            if (key == KeyEvent.VK_SPACE) {
                // Player wishes to jump

                if (p.isJumping || p.isFalling) {
                    return;
                }

                // Timer for creating the jump animation.
                jump = new Timer(5, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        if (p.isPaused) {
                            return;
                        }

                        // Create a rectangle at the player. (for the right)
                        Rectangle rightPlayer = new Rectangle(
                                p.getX() + 90 / 2, p.getY(), PLAYER_WIDTH - 80,
                                PLAYER_HEIGHT - 20);

                        // Create a rectangle at the player. (for the left)
                        Rectangle leftPlayer = new Rectangle(p.getX() + 40, p
                                .getY(), PLAYER_WIDTH / 2, PLAYER_HEIGHT - 20);

                        for (Rectangle r : p.getCurrentLevel().getPlatforms()) {
                            if (r.intersects(rightPlayer)) {
                                p.setX(p.getX() - 2);
                                count = 100;
                            } else if (r.intersects(leftPlayer)) {
                                p.setX(p.getX() + 2);
                                count = 100;
                            }
                        }

                        // Make the player move up by 4 pixels each time the
                        // timer runs.
                        p.setY(p.getY() - 4);
                        // Set the player to be jumping
                        p.isJumping = true;
                        // Disable gravity to allow the player to move upwards.
                        doGravity = false;
                        // Add to the counter, eventually causing the jump to
                        // stop.
                        count += 2;
                        // Once the counter has reached 100, stop the jumping,
                        // and begin falling.
                        if (count >= 100) {
                            // Make sure the timer isn't null.
                            if (jump != null) {
                                // The player is no longer jumping
                                p.isJumping = false;
                                // Enable gravity
                                doGravity = true;
                                // Reset the counter to allow more jumps.
                                count = 0;
                                // Stop the current timer.
                                jump.stop();
                            }
                        }
                    }
                });
                // Start the jump timer.
                jump.start();
            }

        }
    };

    // Instructions panel class.
    class Instructions extends JPanel {

        // Method to paint components on the panel.
        protected void paintComponent(Graphics g) {
            super.paintComponents(g);
            // Paint the background image on the panel.
            g.drawImage(menuBack, 0, 0, null);
        }

        // Constructor for instructions class.
        public Instructions() {
            // Create a layout for the pause panel.
            GridBagLayout layout = new GridBagLayout();
            // Set the layout of the panel to previously created layout.
            setLayout(layout);
            // Get the grid bag constraints from the layout.
            GridBagConstraints gc = layout.getConstraints(this);
            // Set the size of the panel to cover the whole screen.
            setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            // Repaint the screen
            repaint();

            // Create instruction JLabels.
            // Header label for instructions panel.
            JLabel header = new JLabel("<html>INSTRUCTIONS</html>");
            // Set the font to white.
            header.setForeground(Color.WHITE);
            // Set the font to bold, and size to 30.
            header.setFont(new Font(header.getFont().getName(), Font.BOLD, 30));
            // Position the header.
            gc.gridy = 0;
            gc.insets = new Insets(0, 0, 15, 0);
            // Add the header to the panel.
            add(header, gc);

            // Jlabel for instructions.
            JLabel movement = new JLabel("<html>1. Use the <i>arrow keys</i> to navigate the level." +
                    "<br>2. Use the <i>space bar</i> to jump.<br>3." +
                    " Hitting <i>spikes</i> will result in restarting the level.<br>4. Reach " +
                    "the door to complete the level.<br>5. Some platforms <i>move</i>. They can " +
                    "carry you past obstacles.<br><br><center>Press <i>ESC</i> to resume the game.</center></html>");
            // Set the font size to 25.
            movement.setFont(new Font(header.getFont().getName(), Font.PLAIN, 25));
            // Set the colour to white.
            movement.setForeground(Color.white);
            // Position the label under the header.
            gc.gridy = 1;
            // Add the label to the panel.
            add(movement, gc);

        }

    }

    // Pause panel class.
    class PausePanel extends JPanel {

        // Create an action listener to listen for buttons.
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Make sure the event is being fired due to a JButton being
                // pressed.
                if (!(e.getSource() instanceof JButton)) {
                    return;
                }

                // Get the source of the event, cast it to a JButton.
                JButton button = (JButton) e.getSource();

                // Reset the colour of the button for the next pause panel.
                button.setForeground(Color.white);

                // TODO implement instructions.

                // If the player presses resume game button
                if (button.getText().equalsIgnoreCase("Resume Game")) {
                    // Remove the pause panel.
                    tales.remove(pausePanel);
                    // Reset the screen to display the current level.
                    tales.setContentPane(p.getCurrentLevel());
                    // Refresh the JFrame.
                    tales.validate();
                    // Set the game to be no longer paused.
                    p.setPaused(false);
                    // Resume the game timer.
                    p.getCurrentLevel().getGameTimer().resumeTime();
                    // Make all components on the level visible again.
                    for (Component c : p.getCurrentLevel().getComponents()) {
                        c.setVisible(true);
                    }
                    // Set the layout to absolute for component positioning.
                    p.getCurrentLevel().setLayout(null);
                    // Begin the music once again.
                    try {
                        stopMusicFile();
                        playMusicFile("NonBoss.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    return;
                    // Else if the player presses the quit game button.
                } else if (button.getText().equalsIgnoreCase("Quit Game")) {
                    quitGame();
                    // If the user presses the menu button
                } else if (button.getText().equalsIgnoreCase("Return To Menu")) {
                    // Remove the pause panel.
                    tales.remove(pausePanel);
                    // Stop other music, and play menu music.
                    try {
                        stopMusicFile();
                        playMusicFile("MenuMusic.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    // Set the game to be no longer paused.
                    p.setPaused(false);
                    // Reset the time of the current level they were on.
                    p.getCurrentLevel().getGameTimer().resetTime();
                    // Set the JFrame to display the main menu.
                    tales.setContentPane(new MainMenu());
                    // Remove the player from any platforms
                    for (Platform plat : p.getCurrentLevel().getMovingPlats()) {
                        if (plat.hasPlayer()) {
                            plat.hasPlayer = false;
                        }
                    }
                    // Set the current level to be null.
                    p.setCurrentLevel(null);
                    // Set menuscreen visible
                    menu.setVisible(true);
                    // Refresh the JFrame.
                    tales.validate();
                } else if (button.getText().equalsIgnoreCase("Instructions")) {
                    // User wants to view the instructions.
                    tales.remove(pausePanel);
                    tales.setContentPane(new Instructions());
                    tales.validate();
                }

            }
        };

        // Constructor for the pause panel class.
        public PausePanel() {
            // Create a layout for the pause panel.
            GridBagLayout layout = new GridBagLayout();
            // Set the layout of the panel to previously created layout.
            setLayout(layout);
            // Get the grid bag constraints from the layout.
            GridBagConstraints gc = layout.getConstraints(this);
            // Set the background colour to be a transparent dark colour.
            setBackground(new Color(0, 0, 0, 195));
            // Set the size of the panel to cover the whole screen.
            setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

            // Get the main logo image.
            ImageIcon logo = new ImageIcon("Logo.png");
            // Create a JLabel to hold the logo.
            JLabel label3 = new JLabel(logo);
            // Set the size of the JLabel.
            label3.setPreferredSize(new Dimension(1000, 800));
            // Set the location of the image.
            label3.setLocation(SCREEN_WIDTH / 2 - 1000, SCREEN_HEIGHT / 2);
            // Position the JLabel.
            gc.gridx = 0;
            gc.gridy = 0;
            // Give room below the JLabel.
            gc.insets = new Insets(0, 0, 20, 0);
            // Add the JLabel to the pause panel.
            add(label3, gc);

            // Create the JLabel for the Paused Game text.
            JLabel title = new JLabel("Paused Game");
            // Set the font size, and make the text bold.
            title.setFont(new Font(title.getFont().getName(), Font.BOLD, 42));
            // Make the text colour white.
            title.setForeground(Color.white);
            // Position the JLabel.
            gc.gridx = 0;
            gc.gridy = 1;
            // Add the JLabel to pause panel.
            add(title, gc);

            // Create a JButton for resume game.
            JButton resume = new JButton("Resume Game");
            // Set the font size.
            resume.setFont(new Font(resume.getFont().getName(), Font.PLAIN,
                    title.getFont().getSize() - 18));
            // Format the button to make it look nicer.
            resume.setBorderPainted(false);
            resume.setFocusable(false);
            resume.setContentAreaFilled(false);
            // Set the text colour to white.
            resume.setForeground(Color.white);
            // Position the JButton.
            gc.gridy = 2;
            // Add the mouse and action listener.
            resume.addActionListener(listener);
            resume.addMouseListener(ml);
            // Add the JButton to the panel.
            add(resume, gc);

            // Create a JButton for the instructions button.
            JButton instructions = new JButton("Instructions");
            // Set the font size of the button.
            instructions.setFont(new Font(instructions.getFont().getName(),
                    Font.PLAIN, title.getFont().getSize() - 18));
            // Format the button.
            instructions.setBorderPainted(false);
            instructions.setFocusable(false);
            instructions.setContentAreaFilled(false);
            // Set the text colour to white.
            instructions.setForeground(Color.white);
            // Position the button.
            gc.gridy = 3;
            // Add listeners to the button
            instructions.addActionListener(listener);
            instructions.addMouseListener(ml);
            // Add the button to the panel.
            add(instructions, gc);

            // Create return to menu button.
            JButton menu = new JButton("Return To Menu");
            // Set the font of the menu button.
            menu.setFont(new Font(menu.getFont().getName(), Font.PLAIN, title
                    .getFont().getSize() - 18));
            // Format the button.
            menu.setBorderPainted(false);
            menu.setFocusable(false);
            menu.setContentAreaFilled(false);
            // Set the text to be white.
            menu.setForeground(Color.white);
            // Add the listeners, and position the button.
            menu.addActionListener(listener);
            menu.addMouseListener(ml);
            gc.gridy = 4;
            // Add the button to the pause panel.
            add(menu, gc);

            // Create JButton to quit the game.
            JButton quit = new JButton("Quit Game");
            // Set the font size of the button.
            quit.setFont(new Font(quit.getFont().getName(), Font.PLAIN, title
                    .getFont().getSize() - 18));
            // Format the button.
            quit.setBorderPainted(false);
            quit.setFocusable(false);
            quit.setContentAreaFilled(false);
            // Set the text to white.
            quit.setForeground(Color.white);
            // Add the listeners, and position the button.
            quit.addActionListener(listener);
            quit.addMouseListener(ml);
            gc.gridy = 5;
            // Add the button to the JPanel.
            add(quit, gc);

        }

    }

    // Create a mouse listener to catch the hover event.
    static MouseListener ml = new MouseListener() {

        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton button = (JButton) e.getSource();
            }
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        // When the mouse enters the JButton.
        public void mouseEntered(MouseEvent e) {
            // Make sure they are hovering over a button.
            if (!(e.getSource() instanceof JButton)) {
                return;
            }

            // Cast the source to a Jbutton.
            JButton button = (JButton) e.getSource();

            if (button.getText().equalsIgnoreCase("Quit")) {
                button.setForeground(Color.red);
            } else {
                // Set the colour of the button to green.
                button.setForeground(Color.green);
            }

        }

        // When the mouse no longer hovers over the button.
        public void mouseExited(MouseEvent e) {
            // Make sure they are leaving a button.
            if (!(e.getSource() instanceof JButton)) {
                return;
            }

            // Cast the source to a Jbutton.
            JButton button = (JButton) e.getSource();

            // Set the colour of the button to white.
            button.setForeground(Color.white);

        }
    };

    // Main menu panel
    class MainMenu extends JPanel {

        JButton buttonT = new JButton("Tutorial");
        JButton button2 = new JButton("Level 1");
        JButton button3 = new JButton("Level 2");
        JButton button4 = new JButton("Level 3");
        JButton credits = new JButton("Credits");
        JButton instructions = new JButton("Instructions");
        JButton quit = new JButton("Quit");
        JPanel panel1 = new JPanel(), panel2 = new JPanel(),
                panel3 = new JPanel(), panel4 = new JPanel(),
                panel5 = new JPanel();

        JPanel panel = this;

        protected void paintComponent(Graphics g) {

            g.drawImage(menuBack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);

        }

        public MainMenu() {
            final Level tutorial = LevelManager.getLevel(4);
            final Level one = LevelManager.getLevel(1);
            final Level two = LevelManager.getLevel(2);
            final Level three = LevelManager.getLevel(3);

            addKeyListener(kl);

            panel5.setLayout(null);

            panel5.setBackground (Color.lightGray);

            JLabel creditsTitle = new JLabel ("CREDITS");
            creditsTitle.setFont (new Font ("TimesRoman", Font.BOLD+Font.ITALIC, 80));
            creditsTitle.setBounds (SCREEN_WIDTH / 2 - 500 , 150, SCREEN_WIDTH - 500, 100);
            panel5.add(creditsTitle);

            JLabel creditsInfo = new JLabel("<html>Sound Credits:<br>https://www.youtube.com/user/gamingsoundfx<br><br>" +
                    "Knight Image Credits: <br> https://www.google.ca/search?q=knight+image+cartoon&source=lnms&tbm=isch&sa=X&ved=0ahUKEwjXrZOAuL...<br><br>" +
                    "Door Image Credits: <br> http://cliparts.co/cartoon-door <br><br> Dungeon Background Image Credits: <br> " +
                    "http://steamtradingcards.wikia.com/wiki/File:Darkest_Dungeon_Background_The_Ruins.jpg<br><br>" +
                    "Spikes Image Credits: <br> http://sonic.wikia.com/wiki/Spikes_(obstacle)<br><br>Game created by Justin, Alex, and Jerry<html>");

            creditsInfo.setFont (new Font ("TimesRoman", Font.BOLD, 18));
            creditsInfo.setBounds (SCREEN_WIDTH / 2 - 500 , 0, SCREEN_WIDTH - 500, SCREEN_HEIGHT);
            panel5.add(creditsInfo);

            JButton back = new JButton("Main Menu");
            back.setContentAreaFilled(false);
            back.setBorderPainted(false);
            back.setFocusable(false);
            back.setFont(new Font(back.getFont().getName(), Font.PLAIN, 25));
            back.setBounds(SCREEN_WIDTH / 2 - 500 , 0, SCREEN_WIDTH - 900, SCREEN_HEIGHT + 700);
            panel5.add(back);


            ActionListener al = new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    repaint();

                    if (!(event.getSource() instanceof JButton)) {
                        return;
                    }

                    JButton button = (JButton) event.getSource();

                    if(button.getText().equalsIgnoreCase("Main Menu")){
                        tales.setContentPane(new MainMenu());
                        tales.validate();
                        return;
                    }

                    if (button == buttonT) {
                        if (tutorial == null) {
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        panel.setVisible(false);
                        menu.setVisible(false);
                        p.setPaused(false);
                        p.setCurrentLevel(tutorial);
                        tales.setContentPane(tutorial);
                        tales.validate();
                    } else if (button == quit) {
                        System.exit(0);
                    } else if (button == instructions) {
                        button.setForeground(Color.white);
                        tales.setContentPane(new Instructions());
                        tales.validate();
                    } else if (button == credits) {
                        panel.setVisible(false);
                        tales.setContentPane(panel5);
                        tales.validate();
                    } else if (button == button2) {
                        if (one == null) {
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            "Unable to load level!\nHas it been created?",
                                            "Error", 0);
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        p.setCurrentLevel(one);
                        panel.setVisible(false);
                        one.addKeyListener(kl);
                        menu.setVisible(false);
                        // tales.remove(menu);
                        tales.setContentPane(one);
                        tales.validate();
                    } else if (button == button3) {
                        if (two == null) {
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            "Unable to load level!\nHas it been created?",
                                            "Error", 0);
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        p.setCurrentLevel(two);
                        panel.setVisible(false);
                        two.addKeyListener(kl);
                        tales.setContentPane(two);
                        tales.validate();
                    } else {
                        if (three == null) {
                            JOptionPane
                                    .showMessageDialog(
                                            panel,
                                            "Unable to load level!\nHas it been created?",
                                            "Error", 0);
                            System.out.println("[ERROR] Unable to load level.");
                            return;
                        }
                        p.setCurrentLevel(three);
                        panel.setVisible(false);
                        three.addKeyListener(kl);
                        tales.setContentPane(three);
                        tales.validate();
                    }

                    if (p.getCurrentLevel() != null) {
                        for (Component c : p.getCurrentLevel().getComponents()) {
                            c.setVisible(true);
                        }
                        LevelManager.getLevel(4).setLayout(null);
                        p.setX(p.getCurrentLevel().getSpawnX());
                        p.setY(p.getCurrentLevel().getSpawnY());
                        p.getCurrentLevel().getGameTimer().resumeTime();
                        p.canPause = true;
                        p.setCanMove(true);
                        p.getCurrentLevel().setLayout(null);
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

            back.addActionListener(al);

            FlowLayout flow = new FlowLayout();
            this.setLayout(flow);

            Dimension dimB = new Dimension(200, 60);

            p.canPause = false;

            add(quit);
            // panel3.add(back);
            // panel4.add(back);
            // panel5.add(back);
            quit.addActionListener(al);

            buttonT.setContentAreaFilled(false);
            buttonT.setBorderPainted(false);
            buttonT.setFocusable(false);
            buttonT.setForeground(Color.white);
            buttonT.setFont(new Font(buttonT.getFont().getName(), Font.PLAIN,
                    30));
            add(buttonT);
            buttonT.addActionListener(al);
            buttonT.addMouseListener(ml);

            button2.setLocation(100, 100);
            button2.setPreferredSize(dimB);
            button2.setContentAreaFilled(false);
            button2.setBorderPainted(false);
            button2.setFocusable(false);
            button2.setForeground(Color.white);
            button2.setFont(new Font(button2.getFont().getName(), Font.PLAIN,
                    30));
            if (!one.isCompleted()) {
                button2.setForeground(Color.red);
            }
            add(button2);
            button2.addActionListener(al);
            button2.addMouseListener(ml);

            button3.setLocation(100, 100);
            button3.setPreferredSize(dimB);
            button3.setContentAreaFilled(false);
            button3.setBorderPainted(false);
            button3.setForeground(Color.white);
            button3.setFocusable(false);
            button3.setFont(new Font(button3.getFont().getName(), Font.PLAIN,
                    30));
            if (!two.isCompleted()) {
                button3.setForeground(Color.red);
            }
            add(button3);
            button3.addActionListener(al);
            button3.addMouseListener(ml);

            button4.setLocation(100, 100);
            button4.setPreferredSize(dimB);
            button4.setContentAreaFilled(false);
            button4.setBorderPainted(false);
            button4.setForeground(Color.white);
            button4.setFocusable(false);
            button4.setFont(new Font(button4.getFont().getName(), Font.PLAIN,
                    30));
            if (!three.isCompleted()) {
                button4.setForeground(Color.red);
            }
            add(button4);
            button4.addActionListener(al);
            button4.addMouseListener(ml);

            instructions.setLocation(100, 100);
            instructions.setPreferredSize(dimB);
            instructions.setContentAreaFilled(false);
            instructions.setBorderPainted(false);
            instructions.setForeground(Color.white);
            instructions.setFocusable(false);
            instructions.setFont(new Font(instructions.getFont().getName(), Font.PLAIN,
                    30));
            add(instructions);
            instructions.addActionListener(al);
            instructions.addMouseListener(ml);

            quit.setLocation(100, 100);
            quit.setPreferredSize(dimB);
            quit.setContentAreaFilled(false);
            quit.setBorderPainted(false);
            quit.setForeground(Color.white);
            quit.setFocusable(false);
            quit.setFont(new Font(quit.getFont().getName(), Font.PLAIN,
                    30));
            add(quit);
            quit.addActionListener(al);
            quit.addMouseListener(ml);

            credits.setLocation(100, 100);
            credits.setPreferredSize(dimB);
            credits.setContentAreaFilled(false);
            credits.setBorderPainted(false);
            credits.setForeground(Color.white);
            credits.setFocusable(false);
            credits.setFont(new Font(credits.getFont().getName(), Font.PLAIN,
                    30));
            add(credits);
            credits.addActionListener(al);
            credits.addMouseListener(ml);

            panel1.setBackground(new Color(100, 100, 100));

            ImageIcon logo = new ImageIcon("Logo.png");
            JLabel label3 = new JLabel(logo);
            label3.setPreferredSize(new Dimension(1000, 800));
            label3.setLocation(SCREEN_WIDTH / 2 - 1000, SCREEN_HEIGHT / 2);
            add(label3);

        }

    }

    public static void registerLevels() throws IOException {

        // Tutorial
        Rectangle[] spikesTut = {new Rectangle(725, SCREEN_HEIGHT - 50, 120, 25)};
        Rectangle[] tutorialPlats = {
                new Rectangle(400, SCREEN_HEIGHT - 150, 180, 20),
                new Rectangle(1420, SCREEN_HEIGHT - 550, 40, 900),
                new Rectangle(680, SCREEN_HEIGHT - GROUND_WIDTH - 130, 40, 130 + GROUND_WIDTH),
                new Rectangle(855, SCREEN_HEIGHT - GROUND_WIDTH - 180, 40, 180 + GROUND_WIDTH)};
        Level tutorial = new Level(4, 10, SCREEN_HEIGHT - GROUND_WIDTH,
                SCREEN_WIDTH - 400, SCREEN_HEIGHT - GROUND_WIDTH - 50, p,
                tutorialPlats, 1, spikesTut);
        tutorial.addKeyListener(kl);
        Platform tPlat1 = new Platform(1250, 650, 1250, 1030, 90, 30, tutorial,
                1, 2);

        // Level 1
        Rectangle[] spikesOne = {new Rectangle(400, 275, 120, 25), new Rectangle(510, 275, 120, 25), new Rectangle(620, 275, 120, 25), new Rectangle(730, 275, 120, 25),
                new Rectangle(SCREEN_WIDTH - 120, 670, 120, 30),
                new Rectangle(700, 730, 120, 30), new Rectangle(810, 730, 120, 30), new Rectangle(920, 730, 120, 30), new Rectangle(1030, 730, 120, 30), new Rectangle(1140, 730, 150, 30),};
        Rectangle[] onePlats = {new Rectangle(0, 300, 1000, 30),
                new Rectangle(1250, 300, 500, 30),
                new Rectangle(200, 700, 500, 30),
                new Rectangle(1300, 700, 750, 30),
                new Rectangle(700, 760, 600, 30)};
        Level one = new Level(1, 20, 20, SCREEN_WIDTH - 300, SCREEN_HEIGHT
                - GROUND_WIDTH - 50, p, onePlats, 3, spikesOne);
        Platform lPlat2 = new Platform(SCREEN_WIDTH - 120, 100,
                SCREEN_WIDTH - 120, 600, 90, 30, one, 1, 2);
        Platform lPlat3 = new Platform(350, 175, 1150, 175, 90, 30, one, 2, 3);
        Platform lPlat4 = new Platform(600, 600, 1300, 600, 300, 30, one, 3, 2);

        // Level 2
        Rectangle[] spikesTwo = {new Rectangle(370, 270, 120, 30), new Rectangle(480, 270, 120, 30), new Rectangle(590, 270, 120, 30), new Rectangle(700, 270, 120, 30), new Rectangle(810, 270, 120, 30), new Rectangle(920, 270, 120, 30)};
        Rectangle[] twoPlats = {new Rectangle(0, 300, SCREEN_WIDTH - 300, 30),
                new Rectangle(0, 600, SCREEN_WIDTH - 1200, 30),
                new Rectangle(200, 890, 1400, 30)};
        Level two = new Level(2, 40, 150, 100, 425, p, twoPlats, 3, spikesTwo);

        //First platform in level that moves left to right
        Platform Plat1 = new Platform(350, 175, 1050, 175, 90, 30, two, 2, 3);

        //Platform on right that moves up and down
        Platform Plat2 = new Platform(SCREEN_WIDTH - 120, 100, SCREEN_WIDTH - 120, 600, 90, 30, two, 1, 2);

        //Platform in center of screen that
        Platform Plat3 = new Platform(800, 600, 1300, 600, 300, 30, two, 3, 2);

        // Level 3
        Rectangle[] spikesThree = {new Rectangle(370, 270, 120, 30), new Rectangle(480, 270, 120, 30), new Rectangle(590, 270, 120, 30), new Rectangle(700, 270, 120, 30), new Rectangle(810, 270, 120, 30), new Rectangle(920, 270, 120, 30)};
        Rectangle[] threePlats = {new Rectangle(0, 300, SCREEN_WIDTH - 300, 30),
                new Rectangle(0, 600, SCREEN_WIDTH - 1200, 30),
                new Rectangle(200, 890, 1400, 30)};
        Level three = new Level(3, 40, 150, 100, 425, p, threePlats, 0, spikesThree);
    }

    public static void quitGame() {
        // Create variables to catch the result of the confirmation
        // dialog.
        int result = 0;
        // Display a confirmation dialog confirmed the player would
        // like to quit the game.
        int dialog = JOptionPane
                .showConfirmDialog(null,
                        "Are you sure you want to quit?",
                        "Warning", result);
        // If the user does want to quit the game
        if (dialog == JOptionPane.YES_OPTION) {
            // Close the game.
            System.exit(0);
        }
    }

    public static void writeToFile(String text) throws IOException {
        File save = new File("save.txt");
        if (!save.exists()) {
            return;
        }

        PrintWriter out = new PrintWriter(new FileWriter(save, true));
        out.println(text);
        out.close();
    }

    public static void main(String[] args) throws IOException {

        // Create a save file if it does not already exist.
        File save = new File("save.txt");
        if (!save.exists()) {
            // Create the new save file.
            save.createNewFile();
            // Make a writer for the file.
            PrintWriter out = new PrintWriter(save);
            // Print the header in the file.
            out.println("- DUNGEON TALES SAVE FILE -");
            // Close the writer.
            out.close();
            // Create a new instance of a player.
            p = new Player("John");
        } else {
            p = new Player("John");
        }

        // Register levels
        registerLevels();

        // Activate the constructor, and create the frame.
        tales = new DungeonTales();

    }
}
