/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Calendar;

import ishare.Main.Person;
import ishare.Main.Chore;
import ishare.Main.Event;
import ishare.Main.TestFrame;
import ishare.Main.StandardButton;

import java.awt.*;
import javax.swing.*;
import java.util.*;
/**
 *
 * @author Brian Cullinan
 */
public class DayResourcePanel extends JPanel {
    
    protected TestFrame containedBy = null;
    
    int year;
    int month;
    int day;

    public DayResourcePanel(JFrame containedBy, CalendarView.Layout layout, Person person)
    {
        this.containedBy = (TestFrame)containedBy;
        
        this.setLayout(new BorderLayout());
        
        // add time list to side
        if(layout == CalendarView.Layout.THREEDAY || layout == CalendarView.Layout.DAY)
        {
            JPanel times = new JPanel();
            this.add(times);
        }
        // add panel for content
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1));

        // set up most detailed view
        ArrayList<ishare.Main.Event> events = this.containedBy.getEvents(person);
        this.buildEventList(events, content);
        
        // make sure there is at least 1 panel to draw
        if(content.getComponentCount() == 0)
        {
            JPanel msg = new JPanel();
            msg.setLayout(new BorderLayout());
            JLabel text = new JLabel("No Chores to Display");
            msg.add(text, BorderLayout.CENTER);
            content.add(msg);
        }
        
        this.add(content, BorderLayout.CENTER);
    }

    public DayResourcePanel(JFrame containedBy, CalendarView.Layout layout, int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.containedBy = (TestFrame)containedBy;
        
        this.setLayout(new BorderLayout());
        
        // add time list to side
        if(layout == CalendarView.Layout.THREEDAY || layout == CalendarView.Layout.DAY)
        {
            JPanel times = new JPanel();
            this.add(times);
        }
        // add panel for content
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1));
        
        // set up list of resources
        if(layout == CalendarView.Layout.WEEK)
        {
            // set up smaller version of user resource list
        }
        else
        {
            if(layout == CalendarView.Layout.THREEDAY)
            {
                // truncate notes and summary and stuff
                
            }
            else
            {
                if(layout == CalendarView.Layout.DAY)
                {
                    // set up most detailed view
                    ArrayList<ishare.Main.Event> events = this.containedBy.getEvents(year, month, day);
                    this.buildEventList(events, content);
                }
            }
        }
        
        // make sure there is at least 1 panel to draw
        if(content.getComponentCount() == 0)
        {
            JPanel msg = new JPanel();
            msg.setLayout(new BorderLayout());
            JLabel text = new JLabel("No Chores to Display");
            msg.add(text, BorderLayout.CENTER);
            content.add(msg);
        }
        
        this.add(content, BorderLayout.CENTER);
    }
    
    public void buildEventList(ArrayList<ishare.Main.Event> events, JPanel content)
    {
        for(int i = 0; i < events.size(); i++)
        {
            Event e = events.get(i);
            Person person = e.person;
            Chore chore = e.chore;

            if(person != null && chore != null)
            {
                // add a panel for each event
                JPanel event = new JPanel();
                event.setLayout(new BorderLayout());

                // add a user button that goes to profile
                JButton user = new StandardButton(person.getName(), person.getIcon());
                user.setVerticalTextPosition(SwingConstants.BOTTOM);
                user.setHorizontalTextPosition(SwingConstants.CENTER);
                event.add(user, BorderLayout.WEST);

                // add a description button that opens the event
                JLabel eventLabel = new JLabel(person.getName() + " has to do " + chore.getTitle() + " on " + e.month + "/" + e.day + "/" + e.year);
                eventLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
                eventLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                eventLabel.setIcon(chore.getIcon());
                event.add(eventLabel, BorderLayout.CENTER);

                content.add(event);
            }
        }
    }
}
