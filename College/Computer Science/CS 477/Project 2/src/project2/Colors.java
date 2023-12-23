/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project2;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.imageio.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author Brian Cullinan
 */
public class Colors extends JInternalFrame implements ActionListener {
    
    Window window;
    
    HighlightButton red;
    HighlightButton orange;
    HighlightButton yellow;
    HighlightButton green;
    HighlightButton blue;
    HighlightButton purple;
    
    Color selected = Color.RED;
    HighlightButton selected_button;

    public Colors(Window window)
    {
        super();
        this.window = window;
        
        setBounds(0,
               0,
               90,
               window.getHeight() - window.getInsets().top - window.getInsets().bottom);
        setLayout(null);
        setLayer(9999);
        setTitle("Colors");

        // set up color buttons
        red = new HighlightButton("Red");
        red.setBackground(Color.RED);
        red.setBounds(0, 0, 80, 80);
        red.addActionListener(this);
        add(red);
        selected_button = red;
        selected_button.highlighted = true;
        
        orange = new HighlightButton("Orange");
        orange.setBackground(Color.ORANGE);
        orange.setBounds(0, 80, 80, 80);
        orange.addActionListener(this);
        add(orange);
        
        yellow = new HighlightButton("Yellow");
        yellow.setBackground(Color.YELLOW);
        yellow.setBounds(0, 160, 80, 80);
        yellow.addActionListener(this);
        add(yellow);
        
        green = new HighlightButton("Green");
        green.setBackground(Color.GREEN);
        green.setBounds(0, 240, 80, 80);
        green.addActionListener(this);
        add(green);
        
        blue = new HighlightButton("Blue");
        blue.setBackground(Color.BLUE);
        blue.setBounds(0, 320, 80, 80);
        blue.addActionListener(this);
        add(blue);
        
        purple = new HighlightButton("Purple");
        purple.setBackground(Color.magenta);
        purple.setBounds(0, 400, 80, 80);
        purple.addActionListener(this);
        add(purple);
       
        // show toolbar       
        window.add(this);
        setVisible(true);
        //getLayeredPane().getComponent(1).setFont(new Font("Lucida",Font.PLAIN,48));
        //getLayeredPane().getComponent(1).getHeight();
    }
    
    public void actionPerformed(ActionEvent e) {
        if(selected_button != null)
        {
            selected_button.highlighted = false;
            selected_button.repaint();
        }
        selected_button = (HighlightButton)e.getSource();
        selected_button.highlighted = true;
        selected = selected_button.getBackground();
        try {
        if(e.getSource() == red)
            window.tools.stamp.background_image = ImageIO.read(project2.Tools.class.getResource("red.gif"));
        if(e.getSource() == orange)
            window.tools.stamp.background_image = ImageIO.read(project2.Tools.class.getResource("orange.gif"));
        if(e.getSource() == yellow)
            window.tools.stamp.background_image = ImageIO.read(project2.Tools.class.getResource("yellow.gif"));
        if(e.getSource() == green)
            window.tools.stamp.background_image = ImageIO.read(project2.Tools.class.getResource("green.gif"));
        if(e.getSource() == blue)
            window.tools.stamp.background_image = ImageIO.read(project2.Tools.class.getResource("blue.gif"));
        if(e.getSource() == purple)
            window.tools.stamp.background_image = ImageIO.read(project2.Tools.class.getResource("purple.gif"));
        window.tools.stamp.repaint();
        } catch (Exception ex) {
            
        }
    }
    public void paint(Graphics g) {
        super.paint(g);
    }
}
