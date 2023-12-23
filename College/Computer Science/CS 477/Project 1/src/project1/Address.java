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
public class Address extends JToolBar {
    
    JTextField address;
    
    public Address(String value)
    {
        address = new JTextField(value);
        
        setFloatable(false);
        add(new JLabel("Address"));
        add(address);        
        
    }
}
