//==============================================================================
package ishare.People;

import ishare.Calendar.SummaryView;
import ishare.Main.Person;
import ishare.Main.TitledPanel;
import ishare.Main.StandardButton;

import java.awt.*;
import java.awt.event.*;

import java.util.*;
import javax.swing.*;

/*=========================================================================*//**
 * @author Carl Eberhard
 * @version 0.1
 * 
 * Display's person info. Currently only three items (name, icon, and 
 * 	person's calendar info)
 * Forward button on nav panel leads to EditPersonPanel
 * (TODO: incorporate a small version of the CalendarView here)
 * (TODO: wire forward button after creating EditPersonPanel)
 * 
 * (TODO: ??do we need a delete person panel/button?)
 *///===========================================================================
public class PersonInfoPanel extends TitledPanel {

  //the person whose info is shown
	private Person currentPerson = null;
	
	//person basic info
  private JPanel infoPanel = null;
  private JLabel nameLabel = null;
  private JLabel name = null;
  private JLabel icon = null;
  
  //person's calendar info
  private JPanel activityPanel = null;
  private JLabel activityLabel = null;
  private SummaryView calendarPanel = null;
  
	//--------------------------------------------------------------------------
	public PersonInfoPanel( JFrame containedBy, JPanel contextPanel, Person person ){
		super( containedBy, contextPanel );
		debugOutput( "new PersonInfoPanel(" + containedBy + ")" );
		currentPerson = person;
		build();
	}
	
	//--------------------------------------------------------------------------
	protected JPanel buildNavigationPanel(){
            //nav panel with back button to PeopleOverview and forward button to
            //	EditPersonPanel
            debugOutput( "PersonInfoPanel.buildNavigationPanel()" );
            JPanel thisPanel = new JPanel( new BorderLayout() );
		
        backButton = new StandardButton( "Back" );
        backButton.setActionCommand( "Back" );
        backButton.addActionListener( this );
            thisPanel.add( backButton, BorderLayout.WEST );

            titleLabel = new JLabel( currentPerson.getName() + "'s Info" );
            titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
            titleLabel.setFont( titleFont );
            thisPanel.add( titleLabel, BorderLayout.CENTER );

            forwardButton = new StandardButton( "Edit" );
            forwardButton.setActionCommand( "Edit" );
            forwardButton.addActionListener( this );
            thisPanel.add( forwardButton, BorderLayout.EAST );

            navigationPanel = thisPanel;
            return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	protected JPanel buildCenterPanel(){
		//center panel contains the basic info and calendar of the person
		debugOutput( "PersonInfoPanel.buildCenterPanel()" );
		
		BorderLayout centerLayout = new BorderLayout();
		centerLayout.setVgap( STANDARD_GAP ); 
		centerPanel = new JPanel( centerLayout );

		centerPanel.add( buildInfoPanel(), BorderLayout.NORTH );
		centerPanel.add( buildCalendarPanel(), BorderLayout.CENTER );
		return centerPanel;
	}
	
	//--------------------------------------------------------------------------
	private JPanel buildInfoPanel(){
		//info panel contains name and icon of person (non-calendar info)
		debugOutput( "PersonInfoPanel.buildInfoPanel()" );
		
		BorderLayout layout = new BorderLayout();
		layout.setHgap( STANDARD_GAP );
		JPanel thisPanel = new JPanel( layout );
		
		thisPanel.setBorder( createPaddedBorder() );
		
                nameLabel = new JLabel( "Name:" );
		nameLabel.setHorizontalAlignment( SwingConstants.CENTER );
		nameLabel.setFont( labelFont );
		nameLabel.setPreferredSize(
				new Dimension( StandardButton.FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
		thisPanel.add( nameLabel, BorderLayout.WEST );
		
		name = new JLabel( currentPerson.getName() );
		name.setFont( titleFont );
		thisPanel.add( name, BorderLayout.CENTER );
		
		icon = new JLabel( currentPerson.getIcon() );
		thisPanel.add( icon, BorderLayout.EAST );

		infoPanel = thisPanel;
		return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	private JPanel buildCalendarPanel(){
            //calendar panel contains this weeks (?) calendar for the person
            debugOutput( "PersonInfoPanel.buildCalendarPanel()" );

            BorderLayout layout = new BorderLayout();
            layout.setVgap( STANDARD_GAP );
            JPanel thisPanel = new JPanel( layout );

            thisPanel.setBorder( createPaddedBorder() );

            activityLabel = new JLabel( currentPerson.getName() + "'s Calendar" );
            activityLabel.setHorizontalAlignment( SwingConstants.CENTER );
            activityLabel.setFont( labelFont );
            thisPanel.add( activityLabel, BorderLayout.NORTH );

            Calendar calendar = Calendar.getInstance();
            calendarPanel = new SummaryView(containedBy, contextPanel, currentPerson);
            calendarPanel.setBorder( createPaddedBorder() );

            thisPanel.add( calendarPanel, BorderLayout.CENTER );

            activityPanel = thisPanel;
            return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	//no bottom panel - return null
	protected JPanel buildBottomPanel(){ return null; }
	
	//--------------------------------------------------------------------------
	public void actionPerformed( ActionEvent e ){
		debugOutput( "PersonInfoPanel.actionPerfomed: " 
				+ e.getActionCommand() );
		
		//back goes back to the people overview
		if( e.getActionCommand().equals( "Back" ) ){
			containedBy.switchToContextPanel(contextPanel);
			
		} else if ( e.getActionCommand().equals( "Edit" ) ){
			debugOutput( "PersonInfo:  name = " + currentPerson.getName()
					+ ", icon = " + currentPerson.getIcon() );
			containedBy.switchToEditPersonPanel( this, currentPerson );
			
		}
	}
	
	//--------------------------------------------------------------------------
	public Person getCurrentPerson(){ return currentPerson; }
	public void setCurrentPerson( Person person ){
		//sets the current person and all fields which use person info
		debugOutput( "PersonInfoPanel.setCurrentPerson(" + person + ")" );
		currentPerson = person; 

		//title in nav panel
		titleLabel.setText( currentPerson.getName() + "'s Info" );

		//name and icon of the person
		name.setText( currentPerson.getName() );
		icon.setIcon( currentPerson.getIcon() );
		
		//calendar stuff
		activityLabel.setText( currentPerson.getName() + "'s Calendar" );
	  //TODO: set filter on CalendarView
          
	}
}
//==============================================================================
