/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Calendar;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;
import java.awt.event.*;
/**
 *
 * @author Brian Cullinan
 */
public class MonthSelectView extends MonthView {
    
    JPanel contextPanel = null;
    
    public MonthSelectView(JFrame containedBy, JPanel contextPanel, int year, int month, int day)
    {
        super(containedBy, contextPanel, year, month, day);
        
        this.contextPanel = contextPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        
        EventPanel panel = (EventPanel)contextPanel;
        panel.actionPerformed(e);
        
        containedBy.switchToContextPanel(contextPanel);
    }
}
