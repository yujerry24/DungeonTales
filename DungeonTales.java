import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.event.*;
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
        private boolean isPaused;
        private boolean canPause;
        private boolean canMove = true;
        private boolean isMoving = false;
        private boolean isJumping;
        private boolean isFalling;
        private boolean onPlat = false;

        public Player(String name) {
            x = 0;
            y = 0;
            this.name = name;
            this.isVisible = false;
            this.isPaused = false;
            this.canPause = false;
            this.isJumping = false;
            this.isFalling = false;
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

        public boolean canMove() {
            return this.canMove;
        }

        public boolean isPaused() {
            return this.isPaused;
        }

        public Level getCurrentLevel() {
            return this.currentLevel;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setCanMove(boolean canMove) {
            this.canMove = canMove;
        }
        
        public boolean isFalling(){
        	return this.isFalling;
        }
        
        public void setFalling(boolean yes){
        	this.isFalling = yes;
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

        public boolean hasPlayer(){
            return this.hasPlayer;
        }

        public void hasPlayer(boolean yes){
            this.hasPlayer = yes;
        }

        public boolean getBack() {
            return this.back;
        }

        public void setBack(boolean back) {
            this.back = back;
        }

    }

    final static int GROUND_WIDTH = 20;

    static Image door;
    static Image knight;
    static Image knight2;
    static Image knight3;
    static Image pause;
    static Image menuBack;
    static Image spikeImage;

    static int PLAYER_WIDTH = 150;
    static int PLAYER_HEIGHT = 125;

    static class Level extends JPanel {

        private int level;
        private int spawnX, spawnY, endX, endY;
        private boolean isCompleted;
        private Player p;
        private Rectangle[] platforms;
        private Platform[] movingPlats;
        private Rectangle[] spikes ;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(93, 100, 112));
            g.fillRect(0, SCREEN_HEIGHT - GROUND_WIDTH, SCREEN_WIDTH,
                    SCREEN_HEIGHT);
            g.drawImage(menuBack, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
            g.drawImage(door, getEndX(), getEndY(), 187, 187, null);

            for (Rectangle r : getPlatforms()) {

                if (r == null) {
                    return;
                }

                g.fillRect((int) r.getBounds().getMinX(), (int) r.getBounds()
                        .getMinY(), (int) r.getWidth(), (int) r.getHeight());
            }

            for (Rectangle s : getSpikes()) {
                g.drawImage(spikeImage, (int) s.getBounds().getMinX(), (int) s.getBounds()
                        .getMinY(), (int) s.getWidth(), (int) s.getHeight(),null);
            }

            if (p.isVisible()) {
                g.drawImage(knight, p.getX(), p.getY(), 150, 125, null);
            }

            for (Platform p : getMovingPlats()) {

                if (p == null) {
                    return;
                }

                g.fillRect(p.getX(), p.getY(), p.width, p.height);
            }

        }

        public Level(int level, int spawnX, int spawnY, int endX, int endY,
                     final Player p, Rectangle[] platforms, int movingPlats, Rectangle[] spikes) {
            this.level = level;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.endX = endX;
            this.endY = endY;
            this.p = p;
            this.spikes = spikes;

            p.setX(spawnX);
            p.setY(spawnY);
            p.setVisible(true);
            this.platforms = platforms;
            this.isCompleted = false;
            this.movingPlats = new Platform[movingPlats];
            LevelManager.levels[level - 1] = this;

            if (level == 4) {
                setLayout(null);
                // Create the tutorial messages
                JLabel moveTip = new JLabel(
                        "<html><b>TIP:</b><br>Use the arrow keys<br>to navigate the level!</html>");
                moveTip.setBounds(getSpawnX() + 50, getSpawnY() - 160, 300, 100);
                moveTip.setForeground(Color.white);
                moveTip.setFont(new Font(moveTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(moveTip);

                JLabel doorTip = new JLabel(
                        "<html><b>TIP:</b><br>Reach these doors<br>to complete the level!</html>");
                doorTip.setBounds(getEndX() + 50, getSpawnY() - 160, 300, 100);
                doorTip.setForeground(Color.white);
                doorTip.setFont(new Font(doorTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(doorTip);

                JLabel platTip = new JLabel(
                        "<html><b>TIP:</b><br>Jump on these platforms<br>to reach higher parts!</html>");
                platTip.setBounds(SCREEN_WIDTH / 2 - 200, getSpawnY() - 360,
                        300, 100);
                platTip.setForeground(Color.white);
                platTip.setFont(new Font(platTip.getFont().getName(),
                        Font.ITALIC, 20));
                add(platTip);
            }

            ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {

                    // TODO check if the key is being pressed.

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
                                    if(p.hasPlayer()){
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
            return this.isCompleted;
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
            knight2 = ImageIO.read(new File("Knight2.png"));
            knight3 = ImageIO.read(new File("Knight.png"));
            menuBack = ImageIO.read(new File("menuBack.jpg"));
            spikeImage = ImageIO.read(new File("spikes.png"));
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

        movement = new Timer(4, new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (!(p.canMove())) {
                    return;
                }

                if (pressed[0] == 0) {
                    return;
                }

                int key = pressed[0];

                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_LEFT) {
                    if (p.getX() > SCREEN_WIDTH - 140) {
                        p.setX(p.getX() - 2);
                    }
                    if (p.getX() < 10) {
                        p.setX(p.getX() + 2);
                    }
                }

                // Create a rectangle at the player.
                Rectangle player = new Rectangle(p.getX() + 80/2, p.getY(), PLAYER_WIDTH - 80, PLAYER_HEIGHT - 20);
                //p.getCurrentLevel().getGraphics().drawRect(p.getX() + 80/2, p.getY(), PLAYER_WIDTH - 70, PLAYER_HEIGHT - 20);
                
                for(Rectangle r : p.getCurrentLevel().getPlatforms()){
                	if(r.intersects(player) && !p.isFalling){
                		return;
                	}
                }
                
                if (pressed[0] == KeyEvent.VK_LEFT) {
                    p.setX(p.getX() - 2);
                } else if (pressed[0] == KeyEvent.VK_RIGHT) {
                    p.setX(p.getX() + 2);
                }

            }
        });

        // Create a new loop to run gravity.
        Timer gravity = new Timer(5, new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // If the boolean is true, allow gravity to be applied.
                if (doGravity) {

                    // Only apply gravity when the player is within a level which has loaded properly.
                    if (p.getCurrentLevel() == null) {
                        return;
                    }

                    // Create a rectangle at the player.
                    Rectangle player = new Rectangle(p.getX() + PLAYER_WIDTH/2 - 20, p.getY() + PLAYER_HEIGHT - 5, PLAYER_WIDTH/2 - 30, 1);

                    // Test code to draw the location of rectangle.
                    // p.getCurrentLevel().getGraphics().setColor(Color.red);
                    // p.getCurrentLevel().getGraphics().fillRect(p.getX() + PLAYER_WIDTH/2 - 20, p.getY() + PLAYER_HEIGHT - 5, PLAYER_WIDTH/2 - 30, 1);

                    // Loop through all the platforms on the level, and compare their locations with the player.
                    for(Rectangle plat : p.getCurrentLevel().getPlatforms()){
                        if(plat.intersects(player)){
                            // Player is ontop of a platform, so gravity is not applied.
                        	p.setFalling(false);
                            return;
                        }
                    }

                    // Moving platform collision
                    for(Platform plat : p.getCurrentLevel().getMovingPlats()){

                        // Make sure that the platform is not null.
                        if(plat == null){
                            return;
                        }

                        // Create a rectangle out of the platform object.
                        Rectangle r = new Rectangle(plat.getX(), plat.getY(), plat.width, plat.height);

                        // Check if the players rectangle is intersecting with a platform.
                        if(r.intersects(player)){
                            // If they are intersecting, then add the player to that platform.
                            // This is done for moving platforms to allow the player to stay on the platform as it moves.
                            plat.hasPlayer(true);
                            // Return to stop gravity from applying.
                            p.setFalling(false);
                            return;
                        }else{
                            // Else the player is no longer on a platform.
                            // Check to see if the platform that we were checking had the player on top of it.
                            if(plat.hasPlayer()){
                                // The player was on this platform, so remove the player from that platform.
                                plat.hasPlayer(false);
                            }
                        }
                    }

                    // Main gravity control. Sends player to the lowest possible point on the level (Assuming no platform is found)
                    if (p.getY() < SCREEN_HEIGHT - GROUND_WIDTH - 125) {
                        // Make the player fall.
                    	p.setFalling(true);
                        p.setY(p.getY() + 6);
                    }
                }
            }
        });
        // Start the gravity timer.
        gravity.start();
    }

    static Clip clip;
    static MainMenu menu;
    static boolean doGravity = true;
    static Timer movement;

    /*
    Method to play a music file.
    @param The file of music you would like to play.
    @param Boolean for whether or not you would like to loop it.
    @returns Nothing, a void method.
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
    Method to stop any music file currently playing.
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
    Method used to easily obtain stored values from the save file.
    @param The string you would like to get the value of.
    @returns The value as a string.
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

    // Create an array of pressed keys.
    static int[] pressed = new int[1];

    // Create a new key listener to listen for key events.
    static KeyListener kl = new KeyListener() {

        public void keyTyped(KeyEvent arg0) {

        }

        public void keyReleased(KeyEvent e) {
            // Get the key that was released.
            int key = e.getKeyCode();

            // if the player hasn't even moved, then return.
            if(movement == null){
                return;
            }

            // If the key that is released was the left arrow remove it from the array.
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
                // Make sure that the player is on a level, so they cannot pause at the menu screen.
                if (menu.isVisible() || !p.canPause) {
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
                    // Set paused to false - resume all game functions (movement etc.)
                    p.setPaused(false);
                    // if the level is the tutorial level, reappear all the JLabels.
                    if (p.getCurrentLevel().equals(LevelManager.getLevel(4))) {
                        for (Component c : LevelManager.getLevel(4)
                                .getComponents()) {
                            c.setVisible(true);
                        }
                        // Set the tutorial layout to absolute for JLabel positioning.
                        LevelManager.getLevel(4).setLayout(null);
                    }
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
                if (p.getCurrentLevel().equals(LevelManager.getLevel(4))) {
                    // Create a level variable.
                    Level tutorial = LevelManager.getLevel(4);

                    // Hide all the components on the level (JLabels).
                    for (Component c : tutorial.getComponents()) {
                        c.setVisible(false);
                    }
                    // Add a layout to the screen for the pause menu.
                    tutorial.setLayout(new FlowLayout());
                }
                // Set paused to true.
                p.setPaused(true);
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
            if(movement == null){
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
                    knight= knight2;
                }
            }

            if (key == KeyEvent.VK_SPACE) {
                // Player wishes to jump

                if (p.isJumping) {
                    return;
                }

                jump = new Timer(5, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        p.setY(p.getY() - 4);
                        p.isJumping = true;
                        doGravity = false;
                        count += 2;
                        if (count >= 100) {
                            if (jump != null) {
                                p.isJumping = false;
                                doGravity = true;
                                count = 0;
                                jump.stop();
                            }
                        }
                    }
                });
                jump.start();
            }

        }
    };

    static int count = 0;
    static Timer jump;


    static PausePanel pausePanel;

    // Pause panel
    class PausePanel extends JPanel {

        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(e.getSource() instanceof JButton)) {
                    return;
                }

                JButton button = (JButton) e.getSource();

                button.setForeground(Color.white);

                // TODO implement instructions.

                if (button.getText().equalsIgnoreCase("Resume Game")) {
                    tales.remove(pausePanel);
                    tales.setContentPane(p.getCurrentLevel());
                    tales.validate();
                    p.setPaused(false);
                    try {
                        stopMusicFile();
                        playMusicFile("NonBoss.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    return;
                } else if (button.getText().equalsIgnoreCase("Quit Game")) {
                    int result = 0;
                    int dialog = JOptionPane
                            .showConfirmDialog(null,
                                    "Are you sure you want to quit?",
                                    "Warning", result);
                    if (dialog == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                } else if (button.getText().equalsIgnoreCase("Return To Menu")) {
                    tales.remove(pausePanel);
                    try {
                        stopMusicFile();
                        playMusicFile("MenuMusic.wav", true);
                    } catch (IOException ee) {
                    } catch (LineUnavailableException ee) {
                    } catch (UnsupportedAudioFileException ee) {
                    }
                    p.setPaused(false);
                    tales.setContentPane(new MainMenu());
                    tales.validate();
                }

            }
        };

        public PausePanel() {
            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);
            GridBagConstraints gc = layout.getConstraints(this);
            setBackground(new Color(0, 0, 0, 195));
            setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

            ImageIcon logo = new ImageIcon("Logo.png");
            JLabel label3 = new JLabel(logo);
            label3.setPreferredSize(new Dimension(1000, 800));
            label3.setLocation(SCREEN_WIDTH / 2 - 1000, SCREEN_HEIGHT / 2);
            gc.gridx = 0;
            gc.gridy = 0;
            gc.insets = new Insets(0, 0, 20, 0);
            add(label3, gc);

            JLabel title = new JLabel("Paused Game");
            title.setFont(new Font(title.getFont().getName(), Font.BOLD, 42));
            title.setForeground(Color.white);
            gc.gridx = 0;
            gc.gridy = 1;
            add(title, gc);

            JButton resume = new JButton("Resume Game");
            resume.setFont(new Font(resume.getFont().getName(), Font.PLAIN,
                    title.getFont().getSize() - 18));
            resume.setBorderPainted(false);
            resume.setFocusable(false);
            resume.setContentAreaFilled(false);
            resume.setForeground(Color.white);
            gc.gridy = 2;
            resume.addActionListener(listener);
            resume.addMouseListener(ml);
            add(resume, gc);

            JButton instructions = new JButton("Instructions");
            instructions.setFont(new Font(instructions.getFont().getName(),
                    Font.PLAIN, title.getFont().getSize() - 18));
            instructions.setBorderPainted(false);
            instructions.setFocusable(false);
            instructions.setContentAreaFilled(false);
            instructions.setForeground(Color.white);
            gc.gridy = 3;
            instructions.addActionListener(listener);
            instructions.addMouseListener(ml);
            add(instructions, gc);

            JButton menu = new JButton("Return To Menu");
            menu.setFont(new Font(menu.getFont().getName(), Font.PLAIN, title
                    .getFont().getSize() - 18));
            menu.setBorderPainted(false);
            menu.setFocusable(false);
            menu.setContentAreaFilled(false);
            menu.setForeground(Color.white);
            menu.addActionListener(listener);
            menu.addMouseListener(ml);
            gc.gridy = 4;
            add(menu, gc);

            JButton quit = new JButton("Quit Game");
            quit.setFont(new Font(quit.getFont().getName(), Font.PLAIN, title
                    .getFont().getSize() - 18));
            quit.setBorderPainted(false);
            quit.setFocusable(false);
            quit.setContentAreaFilled(false);
            quit.setForeground(Color.white);
            quit.addActionListener(listener);
            quit.addMouseListener(ml);
            gc.gridy = 5;
            add(quit, gc);

        }

    }

    static MouseListener ml = new MouseListener() {

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            if (!(e.getSource() instanceof JButton)) {
                return;
            }

            JButton button = (JButton) e.getSource();

            button.setForeground(Color.green);

        }

        public void mouseExited(MouseEvent e) {
            if (!(e.getSource() instanceof JButton)) {
                return;
            }

            JButton button = (JButton) e.getSource();

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
        JButton back = new JButton("QUIT");
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

            ActionListener al = new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    repaint();

                    if (!(event.getSource() instanceof JButton)) {
                        return;
                    }

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
                        panel.setVisible(false);
                        menu.setVisible(false);
                        p.setPaused(false);
                        p.canPause = true;
                        p.setCurrentLevel(tutorial);
                        tales.setContentPane(tutorial);
                        tales.validate();
                    } else if (button == back) {
                        panel.setVisible(false);
                        tales.setContentPane(panel);
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
                        p.canPause = true;
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
                        p.canPause = true;
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
                        p.canPause = true;
                    }

                    if (p.getCurrentLevel() != null) {
                        for (Component c : p.getCurrentLevel().getComponents()) {
                            c.setVisible(true);
                        }
                        LevelManager.getLevel(4).setLayout(null);
                        p.setX(p.getCurrentLevel().getSpawnX());
                        p.setY(p.getCurrentLevel().getSpawnY());
                    }

                }
            };

            FlowLayout flow = new FlowLayout();
            this.setLayout(flow);

            Dimension dimB = new Dimension(200, 60);

            p.canPause = false;

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
            add(button4);
            button4.addActionListener(al);
            button4.addMouseListener(ml);

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
      
      //Tutorial  
      
      Rectangle[] tutorialPlats = {new Rectangle(800, SCREEN_HEIGHT - 300,
                                                 180, 20), new Rectangle(1420, SCREEN_HEIGHT - 550, 40, 900)};
      
      Rectangle[] spikesOne = {new Rectangle(400,275,500,25), new Rectangle(SCREEN_WIDTH - 180, 600, 180, 100), new Rectangle(700, 730, 500, 30)};
      Rectangle[] spikesTwo = {new Rectangle(100,200)};
      
      Level tutorial = new Level(4, 10, SCREEN_HEIGHT - GROUND_WIDTH - 150,
                                 SCREEN_WIDTH - 400, SCREEN_HEIGHT - GROUND_WIDTH - 100, p,
                                 tutorialPlats, 2, spikesOne);
      tutorial.addKeyListener(kl);
      Platform tPlat1 = new Platform(1250, 750, 1250, 1030, 90, 30, tutorial, 1, 2);
      
      //Level 1
      
      Rectangle[] onePlats = {new Rectangle(0, 300, 1100, 30),new Rectangle (1250, 300, 500, 30),
        new Rectangle(200, 700, 500, 30), new Rectangle(1200, 700, 750, 30), new Rectangle (700, 760, 500, 30)};
      
      //Rectangle[] oneSpikes = {new Rectangle (700, 240, 30, 60)};
      
      Level one = new Level(1, 20, 20, 50, 50, p, onePlats, 3, spikesOne);
      
      tutorial.addKeyListener(kl);
      //Platform lPlat1 = new Platform(1050, 30, 1050, 330, 90, 30, one, 1, 2);
      Platform lPlat2 = new Platform(SCREEN_WIDTH - 120, 100, SCREEN_WIDTH - 120, 600, 90, 30, one, 1, 2);
      Platform lPlat3 = new Platform(350, 175, 950, 175, 90, 30, one, 2, 4);
      Platform lPlat4 = new Platform(600, 600, 1300, 600, 300, 30, one, 3, 2);
        //Level 2
         Rectangle[] twoPlats = {new Rectangle(0, 300, SCREEN_WIDTH - 200, 30),
                new Rectangle(200, 700, SCREEN_WIDTH, 30)};
        Level two = new Level(2, 40, 150, 1700, 880, p, twoPlats, 0, spikesTwo);
    }
}
