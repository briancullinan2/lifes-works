/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project2;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 *
 * @author Brian Cullinan
 */
public class Window extends JDesktopPane implements ComponentListener {
    
    Application application;
    
    Colors colors;
    Tools tools;
    Picture picture;

    public Window(Application application)
    {
        super();
        
        this.application = application;
        application.setContentPane(this);
        addComponentListener(this);
        
        // show the window
        application.setVisible(true);
        
        // set up toolbars
        this.colors = new Colors(this);
        this.tools = new Tools(this);
        
        // set up the paintable area
        this.picture = new Picture(this);
    }
    
    public void componentHidden(ComponentEvent e)
    {

    }
    public void componentMoved(ComponentEvent e)
    {

    }
    public void componentResized(ComponentEvent e)
    {
        if(colors != null)
        colors.setBounds(0,
               0,
               90,
               getHeight() - getInsets().top - getInsets().bottom);
        if(picture != null)
        picture.setBounds(90,
               0,
               getWidth() - getInsets().left - getInsets().right,
               getHeight() - getInsets().top - getInsets().bottom);
        if(tools != null)
        tools.setBounds(getWidth() - getInsets().left - getInsets().right - 90,
               0,
               90,
               getHeight() - getInsets().top - getInsets().bottom);
    }
    public void componentShown(ComponentEvent e)
    {

    }
}
