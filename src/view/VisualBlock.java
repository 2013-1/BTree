package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.concurrent.Semaphore;
import javax.swing.JLabel;

public class VisualBlock extends JLabel {
  private final JLabel text;
  private Semaphore mutex;

  public VisualBlock(String value) {
    super();
    setSize(24, 30);
    text = new JLabel(value);
    initComponents();
  }

  public VisualBlock() {
    super();
    setSize(24, 30);
    text = new JLabel();
    initComponents();
  }

  private void initComponents() {
    text.setSize(getSize());
    Font font = text.getFont();
    setFont(new Font(font.getName(), Font.PLAIN, 14));
    text.setHorizontalAlignment(CENTER);
    text.setVerticalAlignment(CENTER);
    add(text);
    mutex = new Semaphore(1);
  }
  
  @Override
  public void setText(String text){
    super.setText(text);
    try {
      mutex.acquire();
      this.text.setText(text);
      mutex.release();
    }
    catch (Exception e) {
    }
  }
  
  @Override
  public String getText(){
    try {
      mutex.acquire();
      String text1 = this.text.getText();
      mutex.release();
      return text1;
    }
    catch (Exception e) {
      return "";
    }
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D graphics = (Graphics2D) g.create();
    graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    GradientPaint gradient = new GradientPaint(0, 0, new Color(235, 235, 235), getWidth(), getHeight()-10, Color.white);
    graphics.setPaint(gradient);
    graphics.fill(new RegularPolygon(new Point(), getHeight() / 2, 6));
  }
  
  public void highlightON(){
    
  }
}
