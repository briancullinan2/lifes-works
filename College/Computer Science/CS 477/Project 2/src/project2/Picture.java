/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project2;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
/**
 *
 * @author Brian Cullinan
 */

public class Picture extends JInternalFrame implements MouseListener, MouseMotionListener {
    
    protected int current_count = 0;
    
    Window window;
    int last_x = 0;
    int last_y = 0;
    
    BufferedImage red;
    BufferedImage orange;
    BufferedImage yellow;
    BufferedImage green;
    BufferedImage blue;
    BufferedImage purple;
    
    
    Vector<PaintPoint> points = new Vector<PaintPoint>();
    
    public Picture(Window window)
    {
        super();
        this.window = window;

        setBounds(0,
               0,
               window.getWidth() - window.getInsets().left - window.getInsets().right,
               window.getHeight() - window.getInsets().top - window.getInsets().bottom);
        
        // show painting surface
        addMouseListener(this);
        addMouseMotionListener(this);
        window.add(this);
        setVisible(true);
        try {
            setMaximum(true);
        } catch (Exception ex){}
             try {
                red = ImageIO.read(project2.Tools.class.getResource("red.gif"));
                orange = ImageIO.read(project2.Tools.class.getResource("orange.gif"));
                yellow = ImageIO.read(project2.Tools.class.getResource("yellow.gif"));
                green = ImageIO.read(project2.Tools.class.getResource("green.gif"));
                blue = ImageIO.read(project2.Tools.class.getResource("blue.gif"));
                purple = ImageIO.read(project2.Tools.class.getResource("purple.gif"));
            } catch (Exception ex) {

            }
}
    
    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        for(int i = 0; i < points.size(); i++)
        {
            PaintPoint tmp = points.get(i);
            //g.fillRect(i, i, i, i)
            if(tmp.shape == PaintPoint.Shape.ERASER)
            {
                g.setColor(Color.WHITE);
                g.fillRect(tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height);
            }
            else
            {
                if(tmp.shape == PaintPoint.Shape.CIRCLE)
                {
                    g.setColor(tmp.color);
                    g.fillOval(tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height);
                }
                else
                {
                    if(tmp.shape == PaintPoint.Shape.SQUARE)
                    {
                        g.setColor(tmp.color);
                        g.fillRect(tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height);
                    }
                    else
                    {
                        if(tmp.shape == PaintPoint.Shape.HAND)
                        {
                            // TODO: draw hand picture here
                                 try {
                           if(tmp.color == Color.RED)
                            {
                                    g.drawImage(red, tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height, null);
                            }
                           if(tmp.color == Color.ORANGE)
                            {
                                    g.drawImage(orange, tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height, null);
                            }
                           if(tmp.color == Color.YELLOW)
                            {
                                    g.drawImage(yellow, tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height, null);
                            }
                           if(tmp.color == Color.GREEN)
                            {
                                    g.drawImage(green, tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height, null);
                            }
                           if(tmp.color == Color.BLUE)
                            {
                                    g.drawImage(blue, tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height, null);
                            }
                           if(tmp.color == Color.magenta)
                            {
                                    g.drawImage(purple, tmp.position.x-(tmp.size.width/2), tmp.position.y-(tmp.size.height/2), tmp.size.width, tmp.size.height, null);
                            }
                                } catch (Exception ex) {
                                    
                                }
                        }
                    }
                }
            }
        }
        // paint tool
        if(window.tools.selected_button == window.tools.eraser)
        {
            g.drawImage(window.tools.eraser.background_image, last_x, last_y, 50, 50, null);
        }
        if(window.tools.selected_button == window.tools.pen)
        {
            g.drawImage(window.tools.pen.background_image, last_x, last_y, 50, 50, null);
        }
        if(window.tools.selected_button == window.tools.stamp)
        {
            g.drawImage(window.tools.stamp.background_image, last_x, last_y, 50, 50, null);
        }
    }
    
    public void mouseClicked(MouseEvent e)
    {
    }
    public void mouseEntered(MouseEvent e)
    {
        
    }
    public void mouseExited(MouseEvent e)
    {
        
    }
    public void mousePressed(MouseEvent e)
    {
        current_count = 0;
    }
    public void mouseReleased(MouseEvent e)
    {
        window.tools.undo_stack.push(new Integer(current_count));
        window.tools.undo.setEnabled(true);
        window.tools.redo_stack.clear();
        window.tools.redo.setEnabled(false);
        window.tools.tmp_points.clear();
    }
    public void mouseDragged(MouseEvent e)
    {
        last_x = e.getX();
        last_y = e.getY();
        current_count++;
        points.add(new PaintPoint(new Point(e.getX(), e.getY()), window.colors.selected, window.tools.selected));
        repaint();
        e.consume();
    }
    public void mouseMoved(MouseEvent e)
    {
        last_x = e.getX();
        last_y = e.getY();
        this.repaint();
    }
    
}
