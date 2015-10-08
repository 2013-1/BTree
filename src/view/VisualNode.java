/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import primitive.Animation;

/**
 * Classe para representar visualmente um node.
 *
 * @author Yan
 */
public class VisualNode extends JLabel {

  private VisualBlock[] contents;
  private final int lenght;
  private boolean[] connection;

  /**
   *
   * @param size
   */
  public VisualNode(int size) {
    this.lenght = size;
    this.connection = new boolean[size + 1];
    initComponents();
  }

  /**
   *
   * @param value
   * @param index
   */
  public void add(String value, final int index) {
    contents[index].setText(value);

    //animação
    new Thread() {

      @Override
      public void run() {
        try {
          Animation.aniMutex.acquire();
          add(contents[index], 0);
          sleep(1000);
          contents[index].setBorder(null);
        }
        catch (InterruptedException ex) {

        }
        Animation.aniMutex.release();
      }
    }.start();

  }

  private void highlight(final JComponent object, final int time) {
    //animação
    new Thread() {

      @Override
      public void run() {
        try {
          setBackground(new Color(102, 102, 255));
          Animation.aniMutex.acquire();
          sleep(time);
          setBackground(new Color(194, 194, 194));
        }
        catch (InterruptedException ex) {
          Logger.getLogger(VisualNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        Animation.aniMutex.release();
      }
    }.start();
  }

  /**
   *
   * @param index
   */
  public void removeItem(int index) {
    final JLabel garbage = contents[index];

    //animação
    new Thread() {
      @Override
      public void run() {
        try {
          Animation.aniMutex.acquire();
          garbage.setBackground(Color.red);
          garbage.setForeground(Color.white);
          sleep(1000);
          garbage.setText("");
          repaint();
          Animation.aniMutex.release();
        }
        catch (InterruptedException ex) {
          Logger.getLogger(VisualNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NullPointerException e) {
          Animation.aniMutex.release();
        }
      }
    }.start();
  }

  /**
   *
   * @param index
   */
  public void moveLeft(int index) {
    int x = contents[index].getX() - contents[index].getWidth();
    Point location = new Point(x, 0);
    Animation.moveObject(contents[index], location);

    contents[index - 1] = contents[index];
    contents[index] = new VisualBlock();
    contents[index].setLocation(index * contents[index].getWidth() + 3, 0);
    add(contents[index]);
  }

  /**
   *
   * @param index
   */
  public void moveRight(int index) {
    int x = contents[index].getX() + contents[index].getWidth();
    Point location = new Point(x, 0);
    Animation.moveObject(contents[index], location);

    contents[index + 1] = contents[index];
    contents[index] = new VisualBlock();
    contents[index].setLocation(index * contents[index].getWidth() + 3, 0);
    add(contents[index]);
  }

  public void connect(int index) {
    connection[index] = true;
    repaint();
  }

  public void disconnect(int index) {
    connection[index] = false;
    repaint();
  }

  public Point getConnectionPoint(int index) {
    Point location = getLocation();
    try {
      location.x = contents[index].getX() + getX();
    }
    catch (ArrayIndexOutOfBoundsException e) {
      location.x = contents[index - 1].getX() + contents[index - 1].getWidth() + getX();
    }
    location.y = contents[0].getHeight() + getY();
    return location;
  }

  public Point getParentConnectionPoint() {
    Point location = getLocation();
    location.x += (getWidth() - 5) / 2;
    return location;
  }

  private void initComponents() {
    this.contents = new VisualBlock[lenght];
    setLayout(null);

    for (int i = 0; i < contents.length; i++) {
      contents[i] = new VisualBlock();
      VisualBlock block = contents[i];
      block.setLocation(i * block.getWidth() + 3, 0);
      add(block);
    }
    setSize(contents.length * contents[0].getWidth() + 11, contents[0].getHeight() + 5);
    setBackground(new Color(194, 194, 194));

  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D graphics = (Graphics2D) g.create();
    graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setPaint(getBackground());

    RoundRectangle2D rec = new RoundRectangle2D.Float(0, 0, getWidth() - 5, getHeight() - 5, 10, 10);
    Shadow.drawShadow(rec, graphics);
    graphics.fill(rec);

    graphics.setPaint(new Color(102, 102, 255));
    for (int i = 0; i < connection.length; i++) {
      if (connection[i]) {
        int x = 0;
        int height = 0;
        int width = 0;
        try {
          width = contents[i].getWidth();
          height = contents[i].getHeight();
          x = contents[i].getX() - width / 2;
        }
        catch (Exception e) {
          width = contents[i - 1].getWidth();
          height = contents[i - 1].getHeight();
          x = contents[i - 1].getX() - width / 2 + width;
        }

        Polygon triangule = new Polygon();
        triangule.addPoint(x, height);
        triangule.addPoint(x + width / 2, -8 + height);
        triangule.addPoint(x + width, height);

        Area a = new Area(triangule);
        Area b = new Area(rec);
        a.intersect(b);
        graphics.fill(a);

      }
    }//fim laço
    
    graphics.setPaint(new Color(180, 180, 180));
    graphics.draw(rec);
  }

  private Point initialLocation;

  public Point getInitialLocation() {
    return new Point(initialLocation);
  }

  public void setInitialLocation(Point initialLocation) {
    this.initialLocation = new Point(initialLocation);
  }

  public void highlight(int time) {
    highlight(this, time);
  }

  public void highligth(final int index, final int time) {
    //animação
    new Thread() {

      @Override
      public void run() {
        try {
          contents[index].setBackground(Color.blue);
          sleep(time);
          contents[index].setBackground(null);
        }
        catch (InterruptedException ex) {
          Logger.getLogger(VisualNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ArrayIndexOutOfBoundsException er) {

        }
      }
    }.start();
  }

}
