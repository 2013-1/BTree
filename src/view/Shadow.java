package view;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.RoundRectangle2D;

public class Shadow {

  public static void drawShadow(RoundRectangle2D object, Graphics2D graphics) {
    Paint paint = graphics.getPaint();
    graphics.setPaint(new Color(100, 100, 100, 15));
    int translate = 5;
    
    graphics.translate(translate, translate);
    for (int gap = 0; gap < 10; gap+=2) {
      graphics.fillRoundRect(
              (int) object.getX() + gap,
              (int) object.getY() + gap,
              (int) object.getWidth() - gap * 2+translate/3,
              (int) object.getHeight() - gap * 2+translate/2,
              (int) object.getArcWidth(),
              (int) object.getArcHeight()
      );
    }
   graphics.translate(-translate, -translate);
    graphics.setPaint(paint);
  }
}
