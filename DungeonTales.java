/**
 * Auto Generated Java Class.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


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

public class DungeonTales extends JFrame implements ActionListener{

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

 JButton buttonT = new JButton ("Tutorial");
 JButton button2 = new JButton ("Level 1");
   JButton button3 = new JButton ("Level 2");
   JButton button4 = new JButton ("Level 3");
   JButton credits = new JButton ("Credits");
   JButton back = new JButton ("QUIT");
   JFrame frame = new JFrame ();
   JPanel panel = new JPanel (), panel1 = new JPanel (), panel2 = new JPanel (), panel3 = new JPanel (), panel4 = new JPanel (), panel5 = new JPanel (); 
 
 
 
 public DungeonTales() {
  
 frame.setTitle ("Dungeon Tales");
     frame.setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
     frame.setResizable(false);
    
     FlowLayout flow = new FlowLayout();
     setLayout (flow);
  
  Dimension dimB = new Dimension (200 , 60);
  
   back.setLocation (100,100);
    back.setPreferredSize (dimB);
    panel1.add(back);
   // panel2.add(back);
   // panel3.add(back);
   // panel4.add(back);
    //panel5.add(back);
    back.addActionListener(this);
    
    buttonT.setLocation (100,100);
    buttonT.setPreferredSize (dimB);
    panel.add(buttonT);
    buttonT.addActionListener(this);
    
    button2.setLocation (100,100);
    button2.setPreferredSize (dimB);
    panel.add(button2);
    button2.addActionListener(this);
    
    button3.setLocation (100,100);
    button3.setPreferredSize (dimB);
    panel.add(button3);
    button3.addActionListener(this);
    
    button4.setLocation (100,100);
    button4.setPreferredSize (dimB);
    panel.add(button4);
    button4.addActionListener(this);
    
    credits.setLocation (100,100);
    credits.setPreferredSize (dimB);
    panel.add(credits);
    credits.addActionListener (this);
    
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setContentPane(panel);
    frame.setVisible (true); 
    
    panel1.setBackground(new Color (100,100,100));
    
    ImageIcon logo = new ImageIcon ("logo.fw.png");
    JLabel label3 = new JLabel (logo);
    label3.setPreferredSize(new Dimension(1000, 800));
    label3.setLocation (500,500);
    panel.add(label3);
    
    ImageIcon backgroundImg = new ImageIcon ("background2.jpg");
    JLabel label1 = new JLabel (backgroundImg);
    label1.setPreferredSize(new Dimension(SCREEN_WIDTH, 800));
    label1.setLocation (500,500);
    panel1.add(label1);
    
    ImageIcon playerImg = new ImageIcon ("jigglypuff.png");
    JLabel label2 = new JLabel (playerImg);
    label2.setPreferredSize(new Dimension(300, 170));
    label2.setLocation (100,100);
    panel1.add(label2);
  
  
  //setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
  //setResizable(false);
  //setTitle("| Dungeon Tales |");
  
  try {
   playMusicFile("MenuMusic.wav", true);
  } catch (IOException e) {
  } catch (LineUnavailableException e) {
  } catch (UnsupportedAudioFileException e) {
  }
  
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(true);
 }
 
 
 
  public void actionPerformed (ActionEvent event){
    JButton button = (JButton) event.getSource();
    if (button == buttonT){
      frame.remove(panel);
      frame.setContentPane(panel1);
      frame.validate();
      //frame.repaint();
    }
    else if (button == back){
      frame.remove(panel1);
      frame.setContentPane(panel);
      frame.validate();
    }
    else if (button == credits){
      frame.remove(panel);
      frame.setContentPane(panel5);
      frame.validate();
    }
    else if (button == button2){
      frame.remove(panel);
      frame.setContentPane(panel2);
      frame.validate();
    }
    else if (button == button3){
      frame.remove(panel);
      frame.setContentPane(panel3);
      frame.validate();
    }
    else{
      frame.remove(panel);
      frame.setContentPane(panel4);
      frame.validate();
    }
    
    }
  
 
 
 
 
 
 static Clip clip;

 public static void playMusicFile(String file, boolean loop)
   throws IOException, LineUnavailableException,
   UnsupportedAudioFileException {
  File sound = new File(file);

  if(!sound.exists()){
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
  
  //DungeonTales frame = new DungeonTales();
  
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
