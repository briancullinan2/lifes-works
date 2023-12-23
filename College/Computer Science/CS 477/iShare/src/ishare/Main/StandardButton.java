/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Main;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Brian Cullinan
 */
public class StandardButton extends JButton
{

    // these provide some common minimum dimensions for buttons
    public static final int FINGER_WIDTH = 96;
    public static final int FINGER_HEIGHT = 64;

    public StandardButton(String title)
    {
        super("<html><center>" + title + "</center></html>");
        setPreferredSize( new Dimension( FINGER_WIDTH, FINGER_HEIGHT ) );  
    }

    public StandardButton(String title, ImageIcon icon)
    {
        this(title);
        setIcon(icon);
    }
}

