/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Calendar;

import ishare.Main.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
/**
 *
 * @author Brian Cullinan
 */
public class CalendarPanel extends TitledPanel {
    
    JButton users;
    JButton devices;
    JButton add_event;
    
    JButton viewbtn;
    JButton cancel;
    
    CalendarView.Layout layout = null;
    int year, month, day;
    
    public CalendarPanel(JFrame containedBy, JPanel contextPanel, CalendarView.Layout layout, int year, int month, int day)
    {
        super(containedBy, contextPanel);
        
        this.layout = layout;
        this.year = year;
        this.month = month;
        this.day = day;
	build();
    }
    
    protected JPanel buildNavigationPanel()
    {
        //nav panel with forward button leading to AddPersonPanel
        //	(back button unwired)
        JPanel thisPanel = new JPanel( new BorderLayout() );
        debugOutput( "CalendarPanel.buildNavigationPanel()" );

        JPanel westPanel = thisPanel;
        
        if(this.layout != CalendarView.Layout.MONTH && this.layout != CalendarView.Layout.SELECT)
        {
            // add a button for changing the view
            viewbtn = new StandardButton("Month View");
            viewbtn.addActionListener( this );
            
            JLabel spacer = new JLabel("");
            spacer.setPreferredSize( new Dimension( StandardButton.FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );	  
            
            westPanel = new JPanel();
            westPanel.setLayout(new GridLayout(1, 3));
            westPanel.add(viewbtn);
            westPanel.add(spacer);
            
            thisPanel.add(westPanel, BorderLayout.WEST);
        }
        else
        {
            if(this.layout == CalendarView.Layout.SELECT)
            {
                // add a button for changing the view
                cancel = new StandardButton("Cancel");
                cancel.addActionListener( this );

                JLabel spacer = new JLabel("");
                spacer.setPreferredSize( new Dimension( StandardButton.FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );	  

                westPanel = new JPanel();
                westPanel.setLayout(new GridLayout(1, 3));
                westPanel.add(cancel);
                westPanel.add(spacer);

                thisPanel.add(westPanel, BorderLayout.WEST);
            }
        }
        
        backButton = new StandardButton( "Previous" );
        backButton.addActionListener( this );
        westPanel.add( backButton, BorderLayout.WEST );

        titleLabel = new JLabel( "Date" );
        titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
        titleLabel.setFont( titleFont );
        thisPanel.add( titleLabel, BorderLayout.CENTER );

        forwardButton = new StandardButton( "Next" );
        forwardButton.addActionListener( this );
        thisPanel.add( forwardButton, BorderLayout.EAST );

        navigationPanel = thisPanel;
        return thisPanel;
    }
    
    protected JPanel buildCenterPanel(){
        //this center panel holds all the buttons mapped to all known persons 
        //	in the mainFrame's personCollection
        debugOutput( "CalendarPanel.buildCenterPanel()" );

        //BorderLayout layout = new BorderLayout();
        //layout.setVgap( STANDARD_GAP );
        return changeLayout();
    }
    
    public JPanel changeLayout()
    {
        CalendarView thisPanel = null;
        
        switch(layout)
        {
            case DAY:
                thisPanel = new DayView(containedBy, this, year, month, day);
                break;
            case WEEK:
                break;
            case MONTH:
                thisPanel = new MonthView(containedBy, this, year, month, day);
                break;
            case THREEDAY:
                break;
            case SELECT:
                thisPanel = new MonthSelectView(containedBy, contextPanel, year, month, day);
                break;
        }

        if(thisPanel != null)
        {
            thisPanel.setBorder( createPaddedBorder() );

            centerPanel = thisPanel;
            
            setTitle();
        }
        
        return thisPanel;
        
    }
    
    protected JPanel buildBottomPanel(){
        if(centerPanel instanceof MonthSelectView)
            return null;
        
        debugOutput( "CalendarPanel.buildCenterPanel()" );

        //BorderLayout layout = new BorderLayout();
        //layout.setVgap( STANDARD_GAP );
        JPanel thisPanel = new JPanel();
       // set up buttons
        thisPanel.setSize(640, 50);
        
        users = new StandardButton("Manage People");
        users.addActionListener(this);
        devices = new StandardButton("Manage Chores");
        devices.addActionListener(this);
        add_event = new StandardButton("Add Event");
        add_event.addActionListener(this);
       
        thisPanel.add(users);
        thisPanel.add(devices);
        thisPanel.add(add_event);
        
        bottomPanel = thisPanel;
        return thisPanel;
    }
    
    public void setTitle()
    {
        CalendarView view = (CalendarView)centerPanel;
        
        if(view instanceof MonthSelectView)
        {
            Format formatter = new SimpleDateFormat("MMMM, yyyy");
            titleLabel.setText("Select a Date: " + formatter.format(view.calendar.getTime()));
        }
        else
        {
            if(view instanceof DayView)
            {
                Format formatter = new SimpleDateFormat("EEE, d MMMM, yyyy");
                titleLabel.setText(formatter.format(view.calendar.getTime()));
            }
            else
            {
                if(view instanceof MonthView)
                {
                    Format formatter = new SimpleDateFormat("MMMM, yyyy");
                    titleLabel.setText(formatter.format(view.calendar.getTime()));
                }
            }
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        CalendarView view = (CalendarView)centerPanel;
        
        if(e.getSource() == forwardButton)
        {
            if(view instanceof MonthView)
            {
                view.changeDate(view.calendar.get(Calendar.YEAR), view.calendar.get(Calendar.MONTH)+1, view.calendar.get(Calendar.DAY_OF_MONTH));
            }
            else
            {
                if(view instanceof DayView)
                {
                    view.changeDate(view.calendar.get(Calendar.YEAR), view.calendar.get(Calendar.MONTH), view.calendar.get(Calendar.DAY_OF_MONTH)+1);
                }
            }
            setTitle();
        }
        else
        {
            if(e.getSource() == backButton)
            {
                if(view instanceof MonthView)
                {
                    view.changeDate(view.calendar.get(Calendar.YEAR), view.calendar.get(Calendar.MONTH)-1, view.calendar.get(Calendar.DAY_OF_MONTH));
                }
                else
                {
                    if(view instanceof DayView)
                    {
                        view.changeDate(view.calendar.get(Calendar.YEAR), view.calendar.get(Calendar.MONTH), view.calendar.get(Calendar.DAY_OF_MONTH)-1);
                    }
                }
                setTitle();
            }
            else
            {
                if(e.getSource() == viewbtn)
                {
                    // show users panel
                   containedBy.switchToMonthViewPanel(this, year, month, day);
                }
                else
                {
                    if(e.getSource() == users)
                    {
                        // show users panel
                        containedBy.switchToPeoplePanel(this);
                    }
                    else
                    {
                        if(e.getSource() == devices)
                        {
                            // show devices panel
                        	containedBy.switchToChoresPanel(this);
                        }
                        else
                        {
                            if(e.getSource() == add_event)
                            {
                                // show add event panel for current date
                                if(this.layout == CalendarView.Layout.MONTH)
                                    containedBy.switchToEventPanel(this);
                                else
                                     containedBy.switchToEventPanel(this, view.calendar.get(Calendar.YEAR), view.calendar.get(Calendar.MONTH), view.calendar.get(Calendar.DAY_OF_MONTH));
                            }
                            else
                            {
                                if(e.getSource() == cancel)
                                {
                                    // show add event panel for current date
                                    containedBy.switchToContextPanel(contextPanel);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
