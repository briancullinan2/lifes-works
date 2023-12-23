/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project2;

import java.awt.*;
/**
 *
 * @author Brian Cullinan
 */
public class PaintPoint
{
    public enum Shape { CIRCLE, SQUARE, HAND, ERASER }
    Shape shape;
    Point position;
    Dimension size = new Dimension(20, 20);
    Color color;

    public PaintPoint(Point position, Color color, Shape shape)
    {
        this.position = position;
        this.color = color;
        this.shape = shape;
    }
}
