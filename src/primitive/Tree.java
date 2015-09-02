package primitive;

import java.util.logging.Level;
import java.util.logging.Logger;
import view.VisualTree;

public class Tree<Generic> {

  private Node<Generic> root;
  private VisualTree view;

  public Tree(int order) {
    if (order < 4) {
      throw new UnsupportedOperationException("minimum order: 4");
    }
    this.root = new Node(order - 1);
    this.view = new VisualTree(root);
  }

  public void search(Generic value) {
    search(value, root);
  }

  private Node search(Generic value, Node node) {
    node.getView().highlight(1000);
    if (node.isLeaf()) {
      node.getView().highligth(node.indexOf(value), 1000);
      return node;
    }
    int index = node.estimateIndex(value);

    return search(value, node.getNode(index));
  }

  public boolean contains(Generic value) {
    Node node = search(value, root);
    return node.contains(value);
  }

  public void remove(Generic value) throws CoreNode.ValueNotFoundException {
    remove(value, root);
  }

  public Generic remove(Generic value, Node<Generic> node) throws CoreNode.ValueNotFoundException {
    if (node.isLeaf()) {
      try {
        node.remove(value);
        return node.getFirst();
      }
      catch (CoreNode.UnderflowException ex) {
        try {
          updateItem(value, node.getFirst(), node);
          spread(node);
        }
        catch (IndexOutOfBoundsException e) {
          remove(node);
        }
      }
    }
    else {
      int index = node.estimateIndex(value);
      Generic reference = remove(value, node.getNode(index));
      node.overwrite(value, reference);
      return reference;
    }
    return null;
  }

  private void updateItem(Generic oldvalue, Generic newvalue, Node<Generic> node) {
    node.overwrite(oldvalue, newvalue);
    if (!node.isRoot()) {
      updateItem(oldvalue, newvalue, node.getParent());
    }
  }

  public void add(Generic value) {
    Node leaf = search(value, root);
    try {
      leaf.add(value);
    }
    catch (CoreNode.FullNodeException ex) {
      split(leaf);
      add(value);

      Node parent = leaf.getParent();
      int index = parent.estimateIndex(value);
      boolean thisNode = leaf == parent.getNode(index);
      boolean isPair = leaf.capacity() % 2 != 0;
      if (thisNode && isPair) {
        Node brother = parent.getNode(index + 1);
        try {
          brother.add(leaf.removeLast());
        }
        catch (CoreNode.EmptyNodeException | CoreNode.UnderflowException | CoreNode.FullNodeException ex1) {
          Logger.getLogger(Tree.class.getName()).log(Level.SEVERE, null, ex1);

        }
      }

    }
    view.reorganize(root);
  }

  private void add(Node child, Node parent) {
    try {
      parent.connect(child);
    }
    catch (CoreNode.FullNodeException ex) {
      split(child, parent);
    }
    catch (Node.InvalidNodeInsertException ex) {
      ex.printStackTrace();
    }

  }

  private void split(Node node) {
    try {
      Node brother = node.split();
      connectBrother(node, brother);
      view.createNode(brother);
    }
    catch (CoreNode.EmptyNodeException ex) {
      ex.printStackTrace();
    }
  }

  private void split(Node insert, Node node) {
    try {
      split(node);
      Node parent = node.getParent();
      int index = parent.estimateIndex(insert.getFirst());
      add(insert, parent.getNode(index));

      if (parent.getNode(index) == node && node.capacity() % 2 == 0) {
        Node brother = parent.getNode(index + 1);
        brother.connect(node.removeLastNode());
      }

    }
    catch (IndexOutOfBoundsException ex) {
      ex.printStackTrace();
    }
    catch (CoreNode.EmptyNodeException ex) {
      Logger.getLogger(Tree.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (CoreNode.UnderflowException ex) {
      Logger.getLogger(Tree.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (CoreNode.FullNodeException ex) {
      Logger.getLogger(Tree.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (Node.InvalidNodeInsertException ex) {
      Logger.getLogger(Tree.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void spread(Node<Generic> node) {
    remove(node);
    readd(node);
  }

  private void readd(Node<Generic> node) {
    if (node.isLeaf()) {
      for (Generic item : node.toArray()) {
        add(item);
      }
    }
    else {
      for (Node child : node.getConnections()) {
        readd(child);
        view.dropNode(child);
      }
    }
  }

  private void remove(Node<Generic> node) {
    if (node.isRoot()) {
      root = new Node(node.capacity());

      //parte visual
      root.getView().setLocation(node.getView().getLocation());
      view.createNode(root);
    }
    else {
      Node parent = node.getParent();
      try {
        parent.disconnect(node);
      }
      catch (CoreNode.ValueNotFoundException ex) {
        //nada acontece
        System.out.println("passamos por aqui, e não deveria");
      }
      catch (CoreNode.UnderflowException ex) {
        spread(parent);
      }
      catch (NullPointerException np) {
        System.out.println("removendo nó que já foi removido");
        np.printStackTrace();
      }
    }
    view.dropNode(node);
  }

  private Node connectBrother(Node child, Node brother) {
    Node parent;
    if (child.isRoot()) {
      parent = new Node(child.capacity());
      add(child, parent);
      root = parent;

      //parte visual
      parent.getView().setLocation(child.getView().getLocation());
      view.createNode(parent);
    }
    else {
      parent = child.getParent();
    }
    add(brother, parent);

    return parent;
  }

  public void clear() {
    int order = root.capacity();
    root = new Node(order);
    view.clear();
    view.createNode(root);
  }

  public int getOrder() {
    return root.capacity() + 1;
  }

  public void setOrder(int order) {
    this.root = new Node(order - 1);
    view.clear();
    view.createNode(root);
  }

  public VisualTree getVisual() {
    return view;
  }

}
