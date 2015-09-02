/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import primitive.Animation;
import sun.swing.SwingAccessor;

/**
 * Classe para representar visualmente um node.
 *
 * @author Yan
 */
public class VisualNode extends JLabel {

  private JLabel[] nodes, contents;
  private final int lenght;
  private final Dimension square = new Dimension(20, 20);
  private final Color gray = new Color(150, 152, 154);
  private final int thickness = 2;

  /**
   *
   * @param size
   */
  public VisualNode(int size) {
    this.lenght = size;
    initComponents();
  }

  /**
   *
   * @param value
   * @param index
   */
  public void add(String value, int index) {
    contents[index] = new JLabel(value);
    final JLabel content = contents[index];
    Point location = new Point(nodes[index].getX() + nodes[index].getWidth(), thickness);
    content.setSize(square);
    content.setLocation(location);
    content.setHorizontalAlignment(JLabel.CENTER);
    content.setOpaque(true);

    //animação
    new Thread() {

      @Override
      public void run() {
        try {
          Animation.aniMutex.acquire();
          content.setBorder(BorderFactory.createLineBorder(Color.red, thickness));
          add(content, 0);
          sleep(1000);
          content.setBorder(null);
        }
        catch (InterruptedException ex) {
          Logger.getLogger(VisualNode.class.getName()).log(Level.SEVERE, null, ex);
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
          object.setBorder(BorderFactory.createLineBorder(Color.blue, thickness));
          Animation.aniMutex.acquire();
          sleep(time);
          setBorder(BorderFactory.createLineBorder(gray, thickness, true));
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
    contents[index] = null;

    //animação
    new Thread() {
      @Override
      public void run() {
        try {
          Animation.aniMutex.acquire();
          garbage.setBackground(Color.red);
          garbage.setForeground(Color.white);
          sleep(1000);
          remove(garbage);
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
    int x = nodes[index - 1].getX() + nodes[index].getWidth();
    Point location = new Point(x, thickness);
    Animation.moveObject(contents[index], location);

    contents[index - 1] = contents[index];
    contents[index] = null;
  }

  /**
   *
   * @param index
   */
  public void moveRight(int index) {
    int x = nodes[index + 1].getX() + nodes[index].getWidth();
    Point location = new Point(x, thickness);
    Animation.moveObject(contents[index], location);

    contents[index + 1] = contents[index];
    contents[index] = null;
  }

  public void connect(int index) {
    nodes[index].setBackground(Color.blue);
    nodes[index].setBorder(BorderFactory.createLineBorder(Color.blue));
    repaint();
  }

  public void disconnect(int index) {
    nodes[index].setBackground(gray);
    nodes[index].setBorder(BorderFactory.createEmptyBorder());
    repaint();
  }

  public Point getConnectionPoint(int index) {
    JLabel visual = nodes[index];
    Point location = getLocation();
    location.x += visual.getX();
    location.x += visual.getWidth() / 2;
    location.y += visual.getHeight() + thickness + 1;
    return location;
  }

  public Point getParentConnectionPoint() {
    Point location = getLocation();
    location.x += getWidth() / 2;
    return location;
  }

  private void initComponents() {
    this.nodes = new JLabel[lenght + 1];
    this.contents = new JLabel[lenght];

    Dimension nodeSize = new Dimension(square.width / 3, square.height);
    int width = nodes.length * nodeSize.width + contents.length * square.width;
    setSize(width + thickness * 2, square.height + thickness * 2);

    setOpaque(true);
    setBorder(BorderFactory.createLineBorder(gray, thickness, true));
    setVisible(false);

    int gap = square.width + nodeSize.width;
    for (int i = 0; i < nodes.length; i++) {
      nodes[i] = new JLabel();
      JLabel node = nodes[i];
      node.setSize(nodeSize);
      node.setLocation(gap * i + thickness, thickness);
      node.setBackground(gray);
      node.setOpaque(true);
      add(node);
    }
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
        catch(ArrayIndexOutOfBoundsException er){
          
        }
      }
    }.start();
  }

}
