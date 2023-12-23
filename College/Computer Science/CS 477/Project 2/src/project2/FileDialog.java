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
/**
 *
 * @author Brian Cullinan
 */
public class FileDialog extends JDialog implements DocumentListener, ActionListener {
    
    Window window;
    
    JComponent file_name;
    
    JComboBox folder_path;
    
    JButton save;
    JButton load;
    
    public static final String[] keys = {"1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};

    public class KeyInput implements ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            if(((JTextField)file_name).getText().equals("Enter File Name"))
                ((JTextField)file_name).setText("");
            JButton key = (JButton)e.getSource();
            ((JTextField)file_name).setText(((JTextField)file_name).getText() + key.getText());
        }
    }
    
    public FileDialog(Window window, Boolean is_load)
    {
        super();
        this.window = window;
        
        // place in middle of window
        setBounds((window.getWidth() - window.getInsets().left - window.getInsets().right) / 2,
               (window.getHeight() - window.getInsets().top - window.getInsets().bottom) / 2,
               450,
               300);
        this.setLayout(new BorderLayout());
        this.setResizable(true);
        //this.setClosable(true);
        
        // make panel for filepath and folder
        JPanel file = new JPanel();
        file.setLayout(new GridLayout(2, 1));
        this.add(file, BorderLayout.NORTH);
        
        JPanel file_path = new JPanel();
        file_path.setLayout(new BorderLayout());
        file.add(file_path);
        
        JLabel file_str = new JLabel("Filepath:");
        file_path.add(file_str, BorderLayout.WEST);
        
        if(is_load == false)
        {
            file_name = new JTextField("Enter File Name");
            ((JTextField)file_name).getDocument().addDocumentListener(this);
            file_path.add(file_name, BorderLayout.CENTER);

            save = new JButton("Save");
            save.addActionListener(this);
            file_path.add(save, BorderLayout.EAST);
        }
        else
        {
            File directory = new File(System.getProperty("user.home"));
            File[] files = directory.listFiles();
            int count = 0;
            for(int i = 0; i < files.length; i++)
            {
                String filename = files[i].getName();
                if(filename.length() > 10 && filename.substring(filename.length()-10, filename.length()).equals(".easypaint"))
                {
                    count++;
                }
            }
            String[] files_str = new String[count];
            count = 0;
            for(int i = 0; i < files.length; i++)
            {
                String filename = files[i].getName();
                if(filename.length() > 10 && filename.substring(filename.length()-10, filename.length()).equals(".easypaint"))
                {
                    files_str[count] = filename.substring(0, filename.length()-10);
                    count++;
                }
            }
            file_name = new JComboBox(files_str);
            file_path.add(file_name, BorderLayout.CENTER);
                    
            load = new JButton("Load");
            load.addActionListener(this);
            file_path.add(load, BorderLayout.EAST);
        }
        
        JPanel folder = new JPanel();
        folder.setLayout(new BorderLayout());
        file.add(folder);
        JLabel folder_str = new JLabel("Folder:");
        folder.add(folder_str, BorderLayout.WEST);
        
        String[] paths = {System.getProperty("user.home")};
        folder_path = new JComboBox(paths);
        folder.add(folder_path, BorderLayout.CENTER);
        
        // make panel for keys
        JPanel key_panel = new JPanel();
        key_panel.setLayout(new GridLayout(FileDialog.keys.length, 1));
        this.add(key_panel, BorderLayout.CENTER);
        
        KeyInput key_listener = new KeyInput();
        
        // set up keyboard
        for(int i = 0; i < FileDialog.keys.length; i++)
        {
            JPanel row = new JPanel();
            row.setLayout(new GridLayout(1, FileDialog.keys[i].length()));
            key_panel.add(row);
            for(int j = 0; j < FileDialog.keys[i].length(); j++)
            {
                JButton key = new JButton(""+FileDialog.keys[i].charAt(j));
                key.addActionListener(key_listener);
                row.add(key);
            }
        }
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == save)
        {
            try{
                FileOutputStream file = new FileOutputStream(folder_path.getSelectedItem().toString() + "/" + ((JTextField)file_name).getText() + ".easypaint");
                for(int i = 0; i < window.picture.points.size(); i++)
                {
                    file.write(window.picture.points.get(i).color.getRed());
                    file.write(window.picture.points.get(i).color.getGreen());
                    file.write(window.picture.points.get(i).color.getBlue());
                    file.write(window.picture.points.get(i).position.x >> 8);
                    file.write(window.picture.points.get(i).position.x);
                    file.write(window.picture.points.get(i).position.y >> 8);
                    file.write(window.picture.points.get(i).position.y);
                    file.write(window.picture.points.get(i).size.width);
                    file.write(window.picture.points.get(i).size.height);
                    switch(window.picture.points.get(i).shape)
                    {
                        case CIRCLE:
                            file.write(0);
                            break;
                        case SQUARE:
                            file.write(1);
                            break;
                        case HAND:
                            file.write(2);
                            break;
                        case ERASER:
                            file.write(3);
                            break;
                    }
                }
                file.close();
            } catch (Exception ex)
            {
                JOptionPane.showMessageDialog(window,
                "There was a problem saving the file!",
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
            }
            this.setVisible(false);
        }
        else
        {
            if(e.getSource() == load)
            {
                try{
                    window.picture.points.clear();
                    FileInputStream file = new FileInputStream(folder_path.getSelectedItem().toString() + "/" + ((JComboBox)file_name).getSelectedItem().toString() + ".easypaint");
                
                    int buffer = 0;
                    while((buffer = file.read()) != -1)
                    {
                        int red = buffer;
                        int green = file.read();
                        int blue = file.read();
                        Color color = new Color(red, green, blue);
                        int x = file.read() << 8;
                        x += file.read();
                        int y = file.read() << 8;
                        y += file.read();
                        int width = file.read();
                        int height = file.read();
                        int type = file.read();
                        PaintPoint.Shape shape = PaintPoint.Shape.CIRCLE;
                        switch(type)
                        {
                            case 0:
                                shape = PaintPoint.Shape.CIRCLE;
                                break;
                            case 1:
                                shape = PaintPoint.Shape.SQUARE;
                                break;
                            case 2:
                                shape = PaintPoint.Shape.HAND;
                                break;
                            case 3:
                                shape = PaintPoint.Shape.ERASER;
                                break;
                        }
                        PaintPoint point = new PaintPoint(new Point(x, y), color, shape);
                        window.picture.points.add(point);
                    }
                } catch (Exception ex)
                {
                    JOptionPane.showMessageDialog(window,
                    "There was a problem loading the file!",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
                }
                window.picture.repaint();
                this.setVisible(false);
            }
        }
    }
    
    public void changedUpdate(DocumentEvent e) {
    }
    public void removeUpdate(DocumentEvent e) {
    }
    public void insertUpdate(DocumentEvent e) {
        if(((JTextField)file_name).getText().length() > 15 && ((JTextField)file_name).getText().substring(0, 15).equals("Enter File Name"))
        {
            EventQueue.invokeLater(new Runnable()
            {public void run() {
            ((JTextField)file_name).setText(((JTextField)file_name).getText().substring(15));
            }
            });
        }
    }
    
}
