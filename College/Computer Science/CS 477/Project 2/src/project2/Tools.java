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
import java.awt.Dialog.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author Brian Cullinan
 */
public class Tools extends JInternalFrame implements ActionListener {
    
    Window window;
    
    HighlightButton stamp;
    HighlightButton pen;
    HighlightButton eraser;
    
    HighlightButton undo;
    HighlightButton redo;
    
    Stack<Integer> undo_stack = new Stack<Integer>();
    Stack<Integer> redo_stack = new Stack<Integer>();
    
    Vector<PaintPoint> tmp_points = new Vector<PaintPoint>();
    
    HighlightButton save;
    HighlightButton load;
    
    HighlightButton selected_button = stamp;
    PaintPoint.Shape selected = PaintPoint.Shape.CIRCLE;
    
    public Tools(Window window)
    {
        super();
        this.window = window;
        
        setBounds(window.getWidth() - window.getInsets().left - window.getInsets().right - 90,
               0,
               90,
               window.getHeight() - window.getInsets().top - window.getInsets().bottom);
        setLayout(null);
        setLayer(9999);
        setTitle("Tools");
        
        // set up tool buttons
        pen = new HighlightButton("Pen");
        pen.setBounds(0, 0, 80, 80);
        pen.addActionListener(this);
        add(pen);
        selected_button = pen;
        selected_button.highlighted = true;
        
        stamp = new HighlightButton("Stamp");
        stamp.setBounds(0, 80, 80, 80);
        stamp.addActionListener(this);
        add(stamp);
       
        eraser = new HighlightButton("Eraser");
        eraser.setBounds(0, 160, 80, 80);
        eraser.addActionListener(this);
        try {
            eraser.background_image = ImageIO.read(project2.Tools.class.getResource("./eraser.gif"));
            pen.background_image = ImageIO.read(project2.Tools.class.getResource("./pen.gif"));
            stamp.background_image = ImageIO.read(project2.Tools.class.getResource("./red.gif"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window,
                "Some images could not be loaded!",
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
        }
        add(eraser);
        
        undo = new HighlightButton("Undo");
        undo.setBounds(0, 320, 80, 80);
        undo.addActionListener(this);
        undo.setEnabled(false);
        add(undo);
        
        redo = new HighlightButton("Redo");
        redo.setBounds(0, 400, 80, 80);
        redo.addActionListener(this);
        redo.setEnabled(false);
        add(redo);
        
        save = new HighlightButton("Save");
        save.setBounds(0, 560, 80, 80);
        save.addActionListener(this);
        add(save);
        
        load = new HighlightButton("Load");
        load.setBounds(0, 640, 80, 80);
        load.addActionListener(this);
        add(load);
        
        // show toolbar       
        window.add(this);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if((HighlightButton)e.getSource() == pen || (HighlightButton)e.getSource() == stamp || (HighlightButton)e.getSource() == eraser)
        {
            if(selected_button != null)
            {
                selected_button.highlighted = false;
                selected_button.repaint();
            }
            selected_button = (HighlightButton)e.getSource();
            selected_button.highlighted = true;
            
            if(selected_button == pen)
                selected = PaintPoint.Shape.CIRCLE;
            else
                if(selected_button == eraser)
                    selected = PaintPoint.Shape.ERASER;
                else
                    if(selected_button == stamp)
                        selected = PaintPoint.Shape.HAND;
        }
        else
        {
            if((HighlightButton)e.getSource() == undo)
            {
                for(int i = 0; i < undo_stack.peek().intValue(); i++)
                {
                    PaintPoint point = window.picture.points.lastElement();
                    window.picture.points.remove(point);
                    tmp_points.add(point);
                }
                redo_stack.push(undo_stack.pop());
                redo.setEnabled(true);
                if(undo_stack.size() == 0)
                {
                    undo.setEnabled(false);
                }
                window.picture.repaint();
            }
            else
            {
                if((HighlightButton)e.getSource() == redo)
                {
                    for(int i = 0; i < redo_stack.peek().intValue(); i++)
                    {
                        PaintPoint point = tmp_points.lastElement();
                        tmp_points.remove(point);
                        window.picture.points.add(point);
                    }
                    undo_stack.push(redo_stack.pop());
                    undo.setEnabled(true);
                    if(redo_stack.size() == 0)
                    {
                        redo.setEnabled(false);
                    }
                    window.picture.repaint();
                }
                else
                {
                    if((HighlightButton)e.getSource() == save)
                    {
                        FileDialog save_dialog = new FileDialog(window, false);
                        save_dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                        save_dialog.setVisible(true);
                    }
                    else
                    {
                        if((HighlightButton)e.getSource() == load)
                        {
                            FileDialog load_dialog = new FileDialog(window, true);
                            load_dialog.setModalityType(ModalityType.APPLICATION_MODAL);
                            load_dialog.setVisible(true);
                        }
                    }
                }
            }
        }
    }
    public void paint(Graphics g) {
        super.paint(g);
        //g.setColor(Color);
    }
}
