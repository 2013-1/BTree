package primitive;

import java.awt.Color;
import java.awt.Point;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import view.VisualNode;

/**
 * Classe para movimentacao de qualquer JComponent (JPanel, JLabel, etc.) de uma
 * forma generica e rapida. Possui algumas funcoes estaticas para deixa-la
 * pronta pra uso.
 *
 * @author Yan Kaic
 * @since 2015
 */
public class Animation extends Thread {

  private final JComponent object;
  private final Point startPoint;
  private final Point distance;
  private final double sine;
  private final double cosine;
  private final double hypotenuse;
  private static int speed = 4;

  /**
   *
   */
  public static final Semaphore aniMutex = new Semaphore(3);
  private static boolean anitag = false;
  private static final Semaphore mutex = new Semaphore(3);

  /**
   * Construtor padrao para a animacao de um objeto. <br>
   * Este construtor apenas configura como a animacao sera realizada. Para que a
   * animacao comece, basta utilizar a funcao start() de Thread.
   *
   * @param object objeto a ser movimentado
   * @param endPoint ponto que o objeto vai ficar quando a animacao terminar.
   */
  public Animation(JComponent object, Point endPoint) {
    this.object = object;
    startPoint = object.getLocation();

    distance = new Point();
    distance.x = endPoint.x - startPoint.x;
    distance.y = endPoint.y - startPoint.y;

    hypotenuse = startPoint.distance(endPoint);
    sine = distance.y / hypotenuse;
    cosine = distance.x / hypotenuse;
  }

  /**
   * Define o valor do salto da velocidade da animacao. <br>
   * Eh a medida do passo da animacao - nao eh o tempo do sleep da thread.
   * Quanto maior o valor inserido, maior sera a velocidade da animacao.
   *
   * @param speed passo da animacao.
   */
  public static void definirVelocidade(int speed) {
    Animation.speed = speed;
  }

  @Override
  public void run() {
    if (hypotenuse == 0) {
      return;
    }
    try {
      boolean self = false;
      mutex.acquire();
      if (false) {
        aniMutex.acquire();
        anitag = true;
        self = true;
      }
      mutex.release();

      Border originalBorder = object.getBorder();
      object.setBorder(BorderFactory.createLineBorder(Color.red, 2));
      sleep(500);

      //Vai aumentando a distancia e descobrindo seus pontos x e y.
      for (int variableHypotenuse = 0; variableHypotenuse < hypotenuse; variableHypotenuse += speed) {

        int AdjacentCateto = (int) (cosine * variableHypotenuse) + startPoint.x;
        int OppositiveCateto = (int) (sine * variableHypotenuse) + startPoint.y;
        object.setLocation(AdjacentCateto, OppositiveCateto);
        sleep(20);

      }
      //coloca o objeto no ponto final que foi pedido.
      int adjacentCateto = (int) (cosine * hypotenuse) + startPoint.x;
      int oppositiveCateto = (int) (sine * hypotenuse) + startPoint.y;
      object.setLocation(adjacentCateto, oppositiveCateto);

      sleep(500);
      if (object instanceof VisualNode) {
        object.setBorder(BorderFactory.createLineBorder(new Color(150, 152, 154),2,true));
      }
      else {
        object.setBorder(originalBorder);
      }

      if (self) {
        aniMutex.release();
        anitag = false;
      }
    }
    catch (InterruptedException ex) {
      Logger.getLogger(Animation.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Esta funcao permite que um objeto seja movimento sem precisar instanciar o
   * objeto animacao dentro do seu codigo. Basta inserir o objeto e ponto final
   * desejado. Nota: observe a velocidade antes de usar esta funcao.
   *
   * @param objeto objeto a ser movido
   * @param destino ponto que deseja que o objeto fique depois da animacao.
   */
  public static void moveObject(JComponent objeto, Point destino) {
    objeto.setVisible(true);
    Animation animacao = new Animation(objeto, destino);
    animacao.start();
  }

}
