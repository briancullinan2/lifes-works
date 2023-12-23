/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Calendar;

import ishare.Main.Person;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;
import java.awt.event.*;
/**
 *
 * @author Brian Cullinan
 */
public class SummaryView extends CalendarView {
    
    Person person;
    
    public SummaryView(JFrame containedBy, JPanel contextPanel, Person person)
    {
        super(containedBy, contextPanel, 0, 0, 0);
        this.person = person;
        this.setLayout(new GridLayout(1, 1));
        buildCalendar();
    }
    
    public void buildCalendar()
    {
        this.add(new DayResourcePanel(containedBy, CalendarView.Layout.DAY, person));
    }

}
