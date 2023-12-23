/*
 * To change this template, choose Tools | Templates
 * and open the templa
 te in the editor.
 */

package ishare.Calendar;

import ishare.Main.*;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;
import java.awt.event.*;
/**
 *
 * @author Brian Cullinan
 */
public abstract class CalendarView extends JPanel implements ActionListener {
    public enum Layout { DAY, WEEK, MONTH, THREEDAY, SELECT, SUMMARY }
    
    protected TestFrame containedBy = null;
    protected JPanel contextPanel = null;
    
    Calendar calendar = Calendar.getInstance();
    
    Object filter = null;

    public CalendarView(JFrame containedBy, JPanel contextPanel, int year, int month, int day)
    {
        super();
        
        this.containedBy = (TestFrame)containedBy;
        this.contextPanel = contextPanel;
        
        changeDate(year, month, day);
    }
    
    public void clearButtons()
    {
        // remove old date buttons
        for(int i = this.getComponentCount()-1; i >= 0; i--)
        {
            this.remove(i);
        }
    }
    
    public void changeDate(int year, int month, int day)
    {
        // do not set date and build calendar if date is empty
        if(year == 0 && month == 0 && day == 0)
            return;
            
        this.calendar.set(Calendar.DAY_OF_MONTH, 1);
        if(year != this.calendar.get(Calendar.YEAR))
            this.calendar.set(Calendar.YEAR, year);
        if(month != this.calendar.get(Calendar.MONTH))
            this.calendar.set(Calendar.MONTH, month);
        if(day > this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            this.calendar.set(Calendar.DAY_OF_MONTH, this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        else
            this.calendar.set(Calendar.DAY_OF_MONTH, day);
            
        this.clearButtons();
        
        this.buildCalendar();
        
        this.repaint();
    }
    
    abstract protected void buildCalendar();
    
    public void actionPerformed(ActionEvent e) {
        DateButton dbutton = (DateButton)e.getSource();

        containedBy.switchToDayViewPanel(contextPanel, dbutton.year, dbutton.month, dbutton.day);
    }
}
