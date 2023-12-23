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
public class StartMenu extends JInternalFrame implements ActionListener {

    Desktop desktop;
    
    JButton home, downloads;
    
    public StartMenu(Desktop desktop)
    {
        super();
        this.desktop = desktop;

        // this is the panel that holds the special locations and main folders
        setSize(100, 480);
        setLayout(new BorderLayout());
        
        // this is the panel for the special locations (Downloads, etc.)
        JPanel locations = new JPanel();
        locations.setLayout(new GridLayout(0, 1));
        home = new JButton("Home");
        home.addActionListener(this);
        locations.add(home);
        downloads = new JButton("Downloads");
        downloads.addActionListener(this);
        locations.add(downloads);
        add(locations, BorderLayout.NORTH);
        
        // this is the panel for the main directory "quick access"
        JPanel quick_folders = new JPanel();
        quick_folders.setSize(100, 330);
        add(quick_folders, BorderLayout.CENTER);

        desktop.add(this);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == home)
        {
            new Portal(desktop);
        }
    }

}
