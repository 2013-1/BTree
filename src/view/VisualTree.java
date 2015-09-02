/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import primitive.Animation;
import primitive.Node;

/**
 *
 * @author Yan
 */
public class VisualTree extends JPanel {

  private Node root;

  /**
   *
   * @param root
   */
  public VisualTree(Node root) {
    this.root = root;
    createNode(root);
    reloaderScreen();
  }

  /**
   *
   * @param child
   */
  public void createParent(Node child) {
    Point location = child.getView().getLocation();
    Node parent = child.getParent();
    parent.getView().setLocation(location);
    add(parent.getView());

    location.y += 40;
//    Animation.moveObject(child.getVisual(), location);
    child.getView().setLocation(location);
//    generateTree(parent);
  }

  /**
   *
   * @param node
   */
  public final void createNode(Node node) {
    VisualNode view = node.getView();
    add(view);
  }

  public void dropNode(Node node) {
    VisualNode view = node.getView();
    remove(view);
  }



  public void reorganize(Node root) {
    placeTree(root);
    centralizeNodes(root);
    this.root = root;

    int width = getWidth(root);
    Dimension size = getSize();
    size.width = width + 20;
    setPreferredSize(size);
    repaint();
  } 
  
  private int getWidth(Node node) {
    int width = node.isLeaf() ? node.getView().getWidth() : -10;
    for (int x = 0; x < node.connections(); x++) {
      width += getWidth(node.getNode(x)) + 10;
    }
    return width;
  }

  private void centralizeNodes(Node root) {
    root.check();
    VisualNode visual = root.getView();
    Point location = visual.getInitialLocation();
    location.x += getWidth(root) / 2;
    location.x -= visual.getWidth() / 2;

    Animation.moveObject(visual, location);
    for (Node child : root.getConnections()) {
      centralizeNodes(child);
    }
  }

  /**
   *
   * @param node
   */
  private void placeTree(Node node) {

    VisualNode visual = node.getView();
    if (node.isRoot()) {
      visual.setInitialLocation(new Point(10, 10));
    }
    Point location = new Point(visual.getInitialLocation());

    for (int x = 0; x < node.connections(); x++) {
      try {
        Node leftBrother = node.getNode(x - 1);
        location.x = leftBrother.getView().getInitialLocation().x + 10;
        location.x += getWidth(leftBrother);
      }
      catch (IndexOutOfBoundsException ex) {
        location.y = visual.getInitialLocation().y + 80;
      }
      finally {
        Node child = node.getNode(x);
        child.getView().setInitialLocation(location);
        placeTree(child);
      }
    }
  }

  @Override
  public void paintComponent(Graphics graphic) {
    super.paintComponent(graphic);
    drawLines(root, (Graphics2D) graphic.create());
  }

  private void drawLines(Node node, Graphics2D graphic) {
    for (int x = 0; x < node.connections(); x++) {
      Node child = node.getNode(x);
      Point start = node.getView().getConnectionPoint(x);
      Point end = child.getView().getParentConnectionPoint();
      graphic.drawLine(start.x, start.y, end.x, end.y);
      drawLines(child, graphic);
    }
  }

  private void reloaderScreen() {
    new Thread() {
      @Override
      public void run() {
        while (true) {
          try {
            repaint();
            sleep(100);
          }
          catch (InterruptedException ex) {
            Logger.getLogger(VisualTree.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    }.start();
  }

  public void clear() {
    removeAll();
    repaint();
  }

}
