package project2;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



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
        setTitle("Easy Paint");
        setSize(640, 480);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        new Window(this);
    }
}
