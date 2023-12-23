/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project1.ui;

import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultEditorKit;
import java.util.*;

import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.lang.reflect.*;
import java.net.URL;
import java.io.Serializable;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;
import sun.swing.SwingLazyValue;
/**
 *
 * @author Brian Cullinan
 */
public class NewLookAndFeel extends javax.swing.LookAndFeel {
    
    @Override
    public UIDefaults getDefaults() {
        UIDefaults table =
            new MyAuxUIDefaults();
        //Object[] uiDefaults = {
        //  ,
        //};
        table.put("TaskbarUI", "project1.ui.NewTaskbarUI");
        return table;
    }
    
    public String getName() {
        return "New";
    }

    public String getID() {
        return "New";
    }

    public String getDescription() {
        return "My Look and Feel";
    }
    
    public boolean isNativeLookAndFeel() {
        return false;
    }
    
    public boolean isSupportedLookAndFeel() {
        return true;
    }

}
class MyAuxUIDefaults extends UIDefaults {
    @Override
    protected void getUIError(String msg) {
        //System.err.println
        //   ("An annoying message!");
    }
}