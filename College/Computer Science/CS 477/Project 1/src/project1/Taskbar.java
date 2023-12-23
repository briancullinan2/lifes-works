/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project1;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
import project1.ui.*;

/**
 *
 * @author Brian Cullinan
 */
public class Taskbar extends JInternalFrame implements ActionListener {
    
    //private static final String uiClassID = "TaskbarUI";

    Desktop desktop;
    
    JPanel taskbar;
    // the start button on the taskbar
    JButton start;
    
    GridLayout layout;
    
    public Taskbar(Desktop desktop)
    {
        super();
        this.desktop = desktop;
        
        setBounds(0,
               desktop.getHeight() - 50 - desktop.getInsets().top - desktop.getInsets().bottom,
               desktop.getWidth() - desktop.getInsets().left - desktop.getInsets().right,
               50);
        setLayout(new BorderLayout());
        setLayer(9999);
        
        start = new JButton("Start");
        start.addActionListener(this);
        add(start, BorderLayout.WEST);
        
        taskbar = new JPanel();
        layout = new GridLayout(1, 4);
        taskbar.setLayout(layout);
        add(taskbar, BorderLayout.CENTER);

        desktop.add(this);
        setVisible(true);
    }
    
    public JToggleButton add(JInternalFrame frame) {
        
        JToggleButton returnval = new JToggleButton(frame.getTitle());
        if(taskbar.getComponents().length == layout.getColumns())
            layout.setColumns(layout.getColumns()+1);
        taskbar.add(returnval);
        
        return returnval;
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == start)
        {
            desktop.launcher.setVisible(true);
            desktop.launcher.setLocation(0, getY() - desktop.launcher.getHeight());
            desktop.getDesktopManager().activateFrame(desktop.launcher);
            desktop.launcher.toFront();
            try {
                desktop.launcher.setSelected(true);
            }
            catch (Exception ex) {
                System.out.println(ex);
            }
            desktop.launcher.requestFocus();
        }
        
    }
    
    
    //public String getUIClassID() {
    //    return uiClassID;
    //}
}
