/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class MonthView extends CalendarView {
    
    public MonthView(JFrame containedBy, JPanel contextPanel, int year, int month, int day)
    {
        super(containedBy, contextPanel, year, month, day);
    }
    
    public void buildCalendar()
    {
        // add previous months buttons
        int previous_days = this.calendar.get(Calendar.DAY_OF_WEEK) - this.calendar.get(Calendar.DAY_OF_MONTH) % 7;
        if(previous_days < 0) previous_days += 7;
        int after_days = (int)Math.ceil(((double)this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + previous_days) / (double)7) * 7 - (this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + previous_days);
        if(after_days == 7) after_days = 0;

        // adjust layout
        if(previous_days + after_days + this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH) > 35)
        {
            this.setLayout(new GridLayout(6, 7));
        }
        else if(previous_days + after_days + this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH) > 28)
        {
            this.setLayout(new GridLayout(5, 7));
        }
        else
        {
            this.setLayout(new GridLayout(4, 7));
        }

        Calendar tmp_cal = Calendar.getInstance();
        if(this.calendar.get(Calendar.MONTH) - 1 < 0)
            tmp_cal.set(this.calendar.get(Calendar.YEAR) - 1, 12, 1);
        else
            tmp_cal.set(this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH) - 1, 1);
        for(int i = 0; i < previous_days; i++)
        {
            int day = tmp_cal.getActualMaximum(Calendar.DAY_OF_MONTH) - previous_days + i;
            DateButton button = new DateButton(containedBy, tmp_cal.get(Calendar.YEAR), tmp_cal.get(Calendar.MONTH), day+1, DateButton.Type.GRAYED);
            button.addActionListener(this);
            this.add(button);
        }

        // add new date buttons
        //int current_week = this.calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) - 1;
        for(int i = 0; i < this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++)
        {
            DateButton button;
            //if(i >= current_week * 7 && i < (current_week+1) * 7 && i == this.calendar.get(Calendar.DAY_OF_MONTH))
            //{
            //    button = new DateButton(this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH), i+1, DateButton.Type.CURRENT_WEEK);
            //}
            //else
            //{
                if(i+1 == this.calendar.get(Calendar.DAY_OF_MONTH))
                    button = new DateButton(containedBy, this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH), i+1, DateButton.Type.TODAY);
                else                    
                    button = new DateButton(containedBy, this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH), i+1, DateButton.Type.NORMAL);
            //}
            button.addActionListener(this);
            this.add(button);
        }

        // add next months buttons
        if(this.calendar.get(Calendar.MONTH) + 1 > 11)
            tmp_cal.set(this.calendar.get(Calendar.YEAR) + 1, 0, 1);
        else
            tmp_cal.set(this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH) + 1, 1);
        for(int i = 0; i < after_days; i++)
        {
            DateButton button = new DateButton(containedBy, tmp_cal.get(Calendar.YEAR), tmp_cal.get(Calendar.MONTH), i+1, DateButton.Type.GRAYED);
            button.addActionListener(this);
            this.add(button);
        }
        
    }

}
