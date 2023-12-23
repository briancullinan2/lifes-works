/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project1;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;

/**
 *
 * @author Brian Cullinan
 */
public class Desktop extends JDesktopPane implements ComponentListener, InternalFrameListener
{
    Application application;
    Taskbar taskbar;
    StartMenu launcher;
    
    
    public Desktop(Application application)
    {
        super();
        //try {
        //    UIManager.addAuxiliaryLookAndFeel(new project1.ui.NewLookAndFeel());
        //} catch (Exception ex) {
            
        //}
        addComponentListener(this);
        this.application = application;
        application.setContentPane(this);
        application.setVisible(true);
        
        setTaskbar(new Taskbar(this));
        
        setLauncher(new StartMenu(this));
    }
    
    public JInternalFrame add(JInternalFrame frame)
    {
        frame.addInternalFrameListener(this);
        if(frame.getClass() != StartMenu.class && frame.getClass() != StartMenu.class && taskbar != null)
            taskbar.add(frame);
        frame.setIconifiable(false);
        super.add(frame);
        return frame;
    }
        
    public void setTaskbar(Taskbar taskbar)
    {
        this.taskbar = taskbar;
    }
    
    public void setLauncher(StartMenu launcher)
    {
        this.launcher = launcher;
    }
    
    public void componentHidden(ComponentEvent e)
    {

    }
    public void componentMoved(ComponentEvent e)
    {

    }
    public void componentResized(ComponentEvent e)
    {
        if(taskbar != null)
        taskbar.setBounds(0,
               getHeight() - taskbar.getHeight() - getInsets().top - getInsets().bottom,
               getWidth() - getInsets().left - getInsets().right,
               taskbar.getHeight());
    }
    public void componentShown(ComponentEvent e)
    {

    }
    
    public void internalFrameActivated(InternalFrameEvent e) {

    }
    public void internalFrameClosed(InternalFrameEvent e) {

    }
    public void internalFrameClosing(InternalFrameEvent e) {

    }
    public void internalFrameDeactivated(InternalFrameEvent e) {
        if(e.getSource() == launcher)
        {
            launcher.setVisible(false);
        }
    }
    public void internalFrameDeiconified(InternalFrameEvent e) {

    }
    public void internalFrameIconified(InternalFrameEvent e) {

    }
    public void internalFrameOpened(InternalFrameEvent e) {

    }
    
}
