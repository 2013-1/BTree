package primitive;

import java.util.Arrays;
import view.VisualNode;

/**
 * * *********************************************************************
 * Classe que define um no da arvore que possui varios elementos. Este no sempre
 * tera o seu conteudo em ordem crescente.
 *
 * @author Yan Kaic
 * @param <Generic> A classe pode ser de qualquer tipo de dado.
 *
 *** ********************************************************************
 */
public class CoreNode<Generic> {

  private final Generic[] values;
  private final VisualNode view;
  private int size;

  /**
   * Construtor que recebe um tamanho maximo como parametro. Ideal para criar um
   * no raiz, onde nao ha um no pai.
   *
   * @param size tamanho maximo do no
   */
  public CoreNode(int size) {
    this.values = (Generic[]) new Object[size];
    this.view = new VisualNode(size);
    this.size = 0;
  }

  /**
   * Adiciona um item na lista em forma ordenada.
   *
   * @param item item a ser adicionado
   * @throws FullNodeException a node pode estar cheio.
   */
  public void add(Generic item) throws FullNodeException {
    if (isFull()) {
      throw new FullNodeException("full node");
    }
    int index = estimateIndex(item);
    openGap(index);
    values[index] = item;
    size++;

    //parte view
    view.add(item.toString(), index);
  }

  /**
   * Obtem um elemento a partir do indice
   *
   * @param index indice do item
   * @return elemento do indice
   * @throws IndexOutOfBoundsException parametro incorreto
   */
  public Generic get(int index) throws IndexOutOfBoundsException {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("fora da faixa do node. Indice: "+index);
    }
    return values[index];
  }

  /**
   * Obtem o primeiro elemento do node.
   *
   * @return prmeiro elemento
   */
  public Generic getFirst() {
    return get(0);
  }

  /**
   * Obtem o ultimo elemento do node
   *
   * @return ultimo elemento
   * @throws EmptyNodeException o node pode estar vazio
   */
  public Generic getLast() throws EmptyNodeException {
    try {
      return get(size() - 1);
    }
    catch (IndexOutOfBoundsException e) {
      throw new EmptyNodeException("empty node");
    }
  }

  /**
   * Verifica se ha um determinado elemento no node.
   *
   * @param search elemento a ser verificado
   * @return true - caso seja encontrado. false - outro caso
   */
  public boolean contains(Generic search) {
    for (Generic has : values) {
      if (search == has) {
        return true;
      }
    }
    return false;
  }
  
  public void overwrite(Generic oldItem, Generic newItem){
    try {
      int index = indexOf(oldItem);
      this.values[index] = newItem;   
      
      view.removeItem(index);
      view.add(newItem.toString(), index);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      //por enquanto nao faça nada
    }
 
  }

  /**
   * Encontra o indice de determinado elemento
   *
   * @param element elemento a ser procurado
   * @return indice do elemento. Retorna -1 caso nao seja encontrado
   */
  public int indexOf(Generic element) {
    for (int x = 0; x < size; x++) {
      if (element.equals(values[x])) {
        return x;
      }
    }
    return -1;
  }

  /**
   * Remova um item do node.
   *
   * @param item item a ser removido
   * @throws ValueNotFoundException o elemento pode nao estar no node.
   * @throws primitive.CoreNode.UnderflowException
   */
  public void remove(Generic item) throws ValueNotFoundException, UnderflowException {
    try {
      int index = indexOf(item);
      removeIndex(index);
    }
    catch (IndexOutOfBoundsException ex) {
      throw new ValueNotFoundException("item not exist");
    }
  }

  /**
   * Remove um item a partir de sua numeracao
   *
   * @param index indice
   * @return retorna o item removido
   * @throws IndexOutOfBoundsException parametro incorreto
   * @throws UnderflowException
   */
  public Generic removeIndex(int index) throws IndexOutOfBoundsException, UnderflowException {   
    Generic garbage = get(index);
    view.removeItem(index);
    closeGap(index);
    size--;
    
    if (size() < capacity() / 2) {
      throw new UnderflowException("capacidade minima nao preenchida");
    }

    return garbage;
  }

  /**
   * Remove o primeiro elemento do node.
   *
   * @return retorna o primeiro elemento
   * @throws EmptyNodeException a lista pode estar vazia.
   * @throws UnderflowException
   */
  public Generic removeFirst() throws EmptyNodeException, UnderflowException {
    try {
      return removeIndex(0);
    }
    catch (IndexOutOfBoundsException ex) {
      throw new EmptyNodeException("empty node");
    }
  }

  /**
   * Remove o ultimo elemento do node.
   *
   * @return elemento removido
   * @throws EmptyNodeException a lista pode estar vazia.
   * @throws primitive.CoreNode.UnderflowException
   */
  public Generic removeLast() throws EmptyNodeException, UnderflowException {
    try {
      return removeIndex(size - 1);
    }
    catch (IndexOutOfBoundsException ex) {
      throw new EmptyNodeException("empty node");
    }
  }

  /**
   * Retorna a quantidade de elementos que tem no node.
   *
   * @return quantidade de elementos
   */
  public int size() {
    return this.size;
  }

  /**
   * Retorna o tamanho maximo do node.
   *
   * @return tamanho maximo
   */
  public int capacity() {
    return this.values.length;
  }

  /**
   * Verifica se o node esta vazio.
   *
   * @return true - caso esteja vazio. false - outro caso.
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Verifica se o node esta cheio.
   *
   * @return true - caso esteja cheio. false - outro caso.
   */
  public boolean isFull() {
    return size() == capacity();
  }

  /**
   *
   * @return
   */
  public Generic[] toArray() {
    Generic[] array = (Generic[]) new Object[size()];
    System.arraycopy(values, 0, array, 0, size());
    return array;
  }

  /**
   * Move os elementos para a direita a partir do indice.
   *
   * @param index indice de inicio
   */
  private void openGap(int index) {
    for (int i = size(); i > index; i--) {
      values[i] = values[i - 1];
      view.moveRight(i - 1);

    }
  }

  /**
   * Move os elementos para a esquerda a partir do indice.
   *
   * @param index indice de inicio
   */
  private void closeGap(int index) {
    for (int i = index; i < size(); i++) {
      if (i == size - 1) {
        values[i] = null;
        view.removeItem(i);
        view.disconnect(i);
      }
      else {
        values[i] = values[i + 1];
        view.moveLeft(i + 1);
      }
    }
  }

  /**
   *
   * @param element
   * @return
   */
  public int estimateIndex(Generic element) {
    for (int i = 0; i < size(); i++) {
      if (element instanceof Integer) {
        Integer a, b;
        a = ((Integer) element);
        b = ((Integer) values[i]);
        if (a < b) {
          return i;
        }
      }
      else if (element.toString().compareTo(values[i].toString()) <= 0) {
        return i;
      }
    }
    return size();
  }

  /**
   *
   */
  public static class FullNodeException extends Exception {

    /**
     *
     * @param message
     */
    public FullNodeException(String message) {
      super(message);
    }
  }

  /**
   *
   */
  public static class UnderflowException extends Exception {

    /**
     *
     * @param message
     */
    public UnderflowException(String message) {
      super(message);
    }
  }

  /**
   *
   */
  public static class ValueNotFoundException extends Exception {

    /**
     *
     * @param message
     */
    public ValueNotFoundException(String message) {
      super(message);
    }
  }

  /**
   *
   */
  public static class EmptyNodeException extends Exception {

    /**
     *
     * @param message
     */
    public EmptyNodeException(String message) {
      super(message);
    }
  }

  @Override
  public String toString() {
    return Arrays.toString(values);
  }

  /**
   *
   * @return
   */
  public VisualNode getView() {
    return this.view;
  }

}
