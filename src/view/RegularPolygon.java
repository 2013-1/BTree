package view;


import java.awt.Point;
import java.awt.Polygon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Yan
 */
public class RegularPolygon extends Polygon {

  public RegularPolygon(Point location, int radius, int n) {
    final double circunference = 360.0;
    double step = circunference / n;
    Point[] vertexes = new Point[n];
    location.x += radius*0.866;
    location.y += radius;
    int rotation = -90;
    int i = 0;

    for (double angle = rotation; angle < circunference + rotation; angle += step) {
      double radian = angle * Math.PI / 180;
      vertexes[i] = new Point(location);
      vertexes[i].x += (int) (radius * Math.cos(radian));
      vertexes[i].y += (int) (radius * Math.sin(radian));
      i++;
    }

    super.npoints = n;
    super.xpoints = xvector(vertexes);
    super.ypoints = yvector(vertexes);
  }

  private int[] xvector(Point[] points) {
    int[] xvector = new int[points.length];
    
    for (int i = 0; i < points.length; i++) {
      xvector[i] = points[i].x;
    }
    return xvector;
  }

  private int[] yvector(Point[] points) {
    int[] yvector = new int[points.length];
    
    for (int i = 0; i < points.length; i++) {
      yvector[i] = points[i].y;
    }
    return yvector;
  }

}
