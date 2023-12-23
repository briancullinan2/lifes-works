/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Calendar;

import ishare.Main.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
/**
 *
 * @author Brian Cullinan
 */
public class EventPanel extends TitledPanel {
    int year;
    int month;
    int day;
    
    Person user = null;
    Chore chore = null;
    
    JButton userbtn = null;
    JButton chorebtn = null;
    JButton date = null;
    
    JTextField note;
    
    public EventPanel(JFrame containedBy, JPanel contextPanel)
    {
        super(containedBy, contextPanel);
        
	build();
    }
    
    public EventPanel(JFrame containedBy, JPanel contextPanel, int year, int month, int day)
    {
        super(containedBy, contextPanel);
        
        this.year = year;
        this.month = month;
        this.day = day;
        
	build();
                
    }
    
    public EventPanel(JFrame containedBy, JPanel contextPanel, int year, int month, int day, int user, int resource)
    {
        super(containedBy, contextPanel);
        
        this.year = year;
        this.month = month;
        this.day = day;
	build();
        
        // set up edit dialog
    }

    protected JPanel buildNavigationPanel()
    {
        //nav panel with forward button leading to AddPersonPanel
        //	(back button unwired)
        JPanel thisPanel = new JPanel( new BorderLayout() );
        debugOutput( "EventPanel.buildNavigationPanel()" );

        backButton = new StandardButton( "Cancel" );
        backButton.addActionListener( this );
        thisPanel.add( backButton, BorderLayout.WEST );

        titleLabel = new JLabel( "Add Event" );
        titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
        titleLabel.setFont( titleFont );
        thisPanel.add( titleLabel, BorderLayout.CENTER );

        forwardButton = new StandardButton( "Add" );
        forwardButton.addActionListener( this );
        thisPanel.add( forwardButton, BorderLayout.EAST );

        navigationPanel = thisPanel;
        return thisPanel;
    }
    
    protected JPanel buildCenterPanel(){
        //this center panel holds all the buttons mapped to all known persons 
        //	in the mainFrame's personCollection
        debugOutput( "EventPanel.buildCenterPanel()" );
        
        //BorderLayout layout = new BorderLayout();
        //layout.setVgap( STANDARD_GAP );
        JPanel thisPanel = new JPanel( new GridLayout(4, 1) );

        thisPanel.setBorder( createPaddedBorder() );

        // add picker buttons
        // set up user picker
        JPanel user_picker_panel = new JPanel();
        user_picker_panel.setLayout(new FlowLayout());
        JLabel user_label = new JLabel("Pick User: ");
        user_label.setFont( labelFont );
        if(userbtn == null)
            userbtn = new StandardButton("User");
        userbtn.addActionListener(this);
        user_picker_panel.add(user_label);
        user_picker_panel.add(userbtn);
        thisPanel.add(user_picker_panel);
        
        // set up device picker
        JPanel device_picker_panel = new JPanel();
        JLabel device_label = new JLabel("Pick a Chore: ");
        device_label.setFont( labelFont );
        if(chorebtn == null)
            chorebtn = new StandardButton("Chore");
        chorebtn.addActionListener(this);
        device_picker_panel.add(device_label);
        device_picker_panel.add(chorebtn);
        thisPanel.add(device_picker_panel);
        
        // set up date picker panel
        JPanel date_picker_panel = new JPanel();
        JLabel date_label = new JLabel("Pick Date and Time: ");
        date_label.setFont( labelFont );
        if(date == null)
        {
            if(year == 0 && month == 0 && day == 0)
                date = new StandardButton("Calendar");
            else
                date = new StandardButton(month + "/" + day + "/" + year);
        }
        date.addActionListener(this);
        date_picker_panel.add(date_label);
        date_picker_panel.add(date);
        thisPanel.add(date_picker_panel);
        
        // set up note panel
        JPanel note_panel = new JPanel();
        JLabel note_label = new JLabel("Notes: ");
        note_label.setFont( labelFont );
        note = new JTextField();
        note_panel.add(note_label);
        note_panel.add(note);
        thisPanel.add(note_panel);

        centerPanel = thisPanel;
        return thisPanel;
    }
    
    protected JPanel buildBottomPanel(){ return null; }
    
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().equals( "Select Chore" ))
        {
            JButton b = (JButton)e.getSource();
            chore = containedBy.choreCollection.get( b.getText() );

            chorebtn.setText("<html><center>" + chore.getTitle() + "</html></center>");
        }
        else if(e.getActionCommand().equals( "Select Person" ))
        {
            JButton b = (JButton)e.getSource();
            user = containedBy.personCollection.get( b.getText() );

            userbtn.setText("<html><center>" + user.getName() + "</html></center>");
        }
        else if(e.getSource() instanceof DateButton)
        {
            DateButton button = (DateButton)e.getSource();
            year = button.year;
            month = button.month;
            day = button.day;
            
            date.setText("" + month + "/" + day + "/" + year);
        }
        else if(e.getSource() == userbtn)
        {
            // show user dialog
            containedBy.switchToPeopleSelectPanel(this);
        }
        else if(e.getSource() == chorebtn)
        {
            // show chore picker
            containedBy.switchToChoreSelectPanel(this, "Select");
        }
        else if(e.getSource() == date)
        {
            // show date picker
            containedBy.switchToCalendarSelectPanel(this, year, month, day);
        }
        else if(e.getSource() == backButton)
        {
            containedBy.switchToContextPanel(contextPanel);
        }
        else if(e.getSource() == forwardButton)
        {
            // finish adding event
            ishare.Main.Event event = new ishare.Main.Event(year, month, day, user, chore);
            containedBy.events.add(event);
            containedBy.switchToContextPanel(contextPanel);
        }
    }
}
