package primitive;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe para representar um node nao-folha.
 *
 * @author Yan
 * @param <Generic>
 */
public class Node<Generic> extends CoreNode<Generic> {

  private final CoreNode<Node<Generic>> links;
  private Node<Generic> parent;

  /**
   * Construtor de uma node raiz.
   *
   * @param order Tamanho dos nodes serao definidos a partir desse parametro
   */
  public Node(int order) {
    super(order);
    this.links = new CoreNode(order + 1);
  }

  /**
   *
   * @return
   */
  public boolean isLeaf() {
    return links.isEmpty();
  }

  /**
   *
   * @param node
   * @throws FullNodeException
   * @throws InvalidNodeInsertException
   */
  public final void connect(Node<Generic> node) throws FullNodeException, InvalidNodeInsertException {
    try {
      if (!links.isEmpty()) {
        super.add(node.getFirstLeaf().getFirst());
      }
      links.add(node);
      node.setParent(this);

      //parte visual
      getView().connect(links.indexOf(node));
    }
    catch (IndexOutOfBoundsException ex) {
      throw new InvalidNodeInsertException("Tentativa de inserir node invalido");
    }
  }

  /**
   *
   * @param index
   * @return
   * @throws IndexOutOfBoundsException
   */
  public Node<Generic> getNode(int index) throws IndexOutOfBoundsException {
    return links.get(index);
  }

  public void check() {
    for (int x = 0; x < links.capacity(); x++) {
      try {
        getNode(x);
        getView().connect(x);
      }
      catch (IndexOutOfBoundsException ex) {
        getView().disconnect(x);
      }

      Generic first = null;
      try {
        Generic item = get(x);
        first = getNode(x + 1).getFirstLeaf().get(0);
        if (item != first) {
          remove(item);
          add(first);
        }
      }
      catch (ValueNotFoundException | FullNodeException e) {
        e.printStackTrace();
      }
      catch (IndexOutOfBoundsException ex) {
        //vai acontecer propositalmente
      }
      catch (UnderflowException ex) {
        try {
          add(first);
        }
        catch (FullNodeException ex1) {
          ex1.printStackTrace();
        }
      }

    }
  }

  /**
   *
   * @return
   */
  public Node[] getConnections() {
    Node<Generic>[] array = new Node[connections()];
    for (int x = 0; x < connections(); x++) {
      array[x] = links.get(x);
    }
    return array;
  }

  /**
   *
   * @return @throws EmptyNodeException
   * @throws UnderflowException
   */
  public Node<Generic> removeLastNode() throws EmptyNodeException, UnderflowException {
    Node<Generic> garbage = links.removeLast();
    removeLast();
    return garbage;
  }

  /**
   *
   * @return
   */
  public int connections() {
    return links.size();
  }

  /**
   *
   * @param node
   * @throws ValueNotFoundException
   * @throws primitive.CoreNode.UnderflowException
   */
  public void disconnect(Node<Generic> node) throws ValueNotFoundException, UnderflowException {

    try {
      int index = links.indexOf(node);
      links.remove(node);
      super.remove(node.getFirst());
      node.setParent(null);
      getView().disconnect(index);
    }
    catch (IndexOutOfBoundsException ex) {
      throw new ValueNotFoundException("removendo no vazio");
    }

  }

  public Node<Generic> disconnect(int index) throws UnderflowException, IndexOutOfBoundsException {
    Node<Generic> garbage = links.removeIndex(index);
    removeIndex(index - 1);
    garbage.setParent(null);
    return garbage;
  }

  public Node<Generic> split() throws EmptyNodeException {
    Node halfNode = new Node(capacity());
    halfNode.getView().setLocation(getView().getLocation());
    try {
      if (isLeaf()) {
        int half = (capacity() + 1) / 2;
        while (size() > half) {
          halfNode.add(removeIndex(half));
        }
      }
      else {
        int half = (links.capacity()+1) / 2;
        while (connections() >= half) {
          halfNode.connect(disconnect(half));
        }
      }
    }
    catch (FullNodeException | InvalidNodeInsertException | UnderflowException e) {
      throw new EmptyNodeException("no vazio");
    }
    catch (IndexOutOfBoundsException ex){
      //nada a fazer
    }
    return halfNode;
  }

  public Node<Generic> getFirstLeaf() {
    if (isLeaf()) {
      return this;
    }
    return getFirstNode().getFirstLeaf();
  }

  public Node<Generic> getFirstNode() {
    return getNode(0);
  }

  /**
   *
   */
  public static class InvalidNodeInsertException extends Exception {

    /**
     *
     * @param message
     */
    public InvalidNodeInsertException(String message) {
      super(message);
    }
  }

  @Override
  public String toString() {
    if (getFirst() instanceof Integer) {
      DecimalFormat df = new DecimalFormat("00");
      return df.format(getFirst());
    }
    return getFirst().toString();
  }

  /**
   *
   * @return
   */
  public Node<Generic> getParent() {
    return parent;
  }

  /**
   *
   * @param parent
   */
  public void setParent(Node<Generic> parent) {
    this.parent = parent;
  }

  /**
   *
   * @return
   */
  public boolean isRoot() {
    return parent == null;
  }

}
