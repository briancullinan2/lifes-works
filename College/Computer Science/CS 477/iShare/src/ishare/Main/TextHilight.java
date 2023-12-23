/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Brian Cullinan
 */
public class TextHilight extends JTextField implements FocusListener {
    
    public TextHilight(String text)
    {
        super(text);
        this.addFocusListener(this);
    }
    
    public void focusGained(FocusEvent e)
    {
        this.setBackground(new Color(255, 255, 128));
    }
    
    public void focusLost(FocusEvent e)
    {
        this.setBackground(new Color(255, 255, 255));
    }

}
