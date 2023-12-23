/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project1;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Brian Cullinan
 */
public class Portal extends JInternalFrame {
    
    Desktop desktop;
    
    public Portal(Desktop desktop)
    {
        super();
        this.desktop = desktop;
        
        setTitle("Portal");
        setResizable(true);
        setClosable(true);
        setMaximizable(true);
        setLayout(new BorderLayout());
        setSize(540, 480);
        
        JPanel tool_container = new JPanel();
        tool_container.setLayout(new GridLayout(0, 1));
        add(tool_container, BorderLayout.NORTH);
        
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        tool_container.add(tools, BorderLayout.NORTH);
        // these are some buttons for controlling the folder
        JButton back = new JButton("Back");
        tools.add(back);
        JButton forward = new JButton("Forward");
        tools.add(forward);
        JButton up = new JButton("Up");
        tools.add(up);
        JButton refresh = new JButton("Refresh");
        tools.add(refresh);
        JButton search = new JButton("Search");
        tools.add(search);
        JButton folders = new JButton("Folders");
        tools.add(folders);
        JButton display = new JButton("View");
        tools.add(display);
        
        // the main address of the current folder
        Address address = new Address("/");
        tool_container.add(address, BorderLayout.NORTH);
  
        // create a view for the files
        FolderView view = new FolderView(address);
        add(view, BorderLayout.CENTER);
                
        // this is the folder tasks panel
        JPanel tasks = new JPanel();
        tasks.setLayout(new GridLayout(0, 1));
        add(tasks, BorderLayout.WEST);
        
        JPanel folder_tasks = new JPanel();
        folder_tasks.setLayout(new FlowLayout());
        tasks.add(folder_tasks);
        
        JButton send_to_downloads = new JButton("Send Selected File(s) to Downloads");
        folder_tasks.add(send_to_downloads);
        
        desktop.add(this);
        setVisible(true);
    }
}
