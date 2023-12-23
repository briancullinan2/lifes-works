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
public class Application extends JFrame {

    public Application()
    {
        super();
        setTitle("Media Server");
        setSize(640, 480);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        new Desktop(this);
    }
}
