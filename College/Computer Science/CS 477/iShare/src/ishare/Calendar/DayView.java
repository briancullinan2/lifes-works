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
public class DayView extends CalendarView {
    
    public DayView(JFrame containedBy, JPanel contextPanel, int year, int month, int day)
    {
        super(containedBy, contextPanel, year, month, day);
        this.setLayout(new GridLayout(1, 1));
    }
    
    public void buildCalendar()
    {
        this.add(new DayResourcePanel(containedBy, CalendarView.Layout.DAY, this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH), this.calendar.get(Calendar.DAY_OF_MONTH)));
    }
}
