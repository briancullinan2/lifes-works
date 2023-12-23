//==============================================================================
package ishare.People;

import ishare.Main.*;
import ishare.Calendar.EventPanel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*=========================================================================*//**
 * @author Carl Eberhard
 * @version 0.1
 * 
 * Presents all known persons (in the personCollection in the main frame)
 *  as a series of buttons which, when pressed, send the user to the
 *  PersonInfoPanel for that specific person.
 * Top right (forward button on the navigation panel) sends user to the
 * 	AddPersonPanel.
 * 
 * 
 *///===========================================================================
public class PeopleSelectPanel extends PeoplePanel {
	
  private JPanel peoplePanel = null;
  private JPanel peopleButtonPanel = null;
  
	//--------------------------------------------------------------------------
	public PeopleSelectPanel( JFrame containedBy, JPanel contextPanel ){
		super( containedBy, contextPanel );
		debugOutput( "new PeopleOverviewPanel(" + containedBy + ")" );
		
		build();
	}
	
	//--------------------------------------------------------------------------
	protected JPanel buildNavigationPanel(){
		//nav panel with forward button leading to AddPersonPanel
		//	(back button unwired)
		JPanel thisPanel = new JPanel( new BorderLayout() );
		debugOutput( "PeopleOverviewPanel.buildNavigationPanel()" );
		
                backButton = new StandardButton( "Cancel" );
		backButton.setActionCommand( "Back" );
		backButton.addActionListener( this );
		thisPanel.add( backButton, BorderLayout.WEST );
		
		titleLabel = new JLabel( "Everybody" );
		titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
		titleLabel.setFont( titleFont );
		thisPanel.add( titleLabel, BorderLayout.CENTER );

		navigationPanel = thisPanel;
		return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	class PersonButton extends JButton {
		//inner class for building a button with a person's name and icon
		//	and mapping to a person
		
		Person person = null;

		public PersonButton( Person person, ActionListener actionListener ){
			super();
			this.person = person;
			
			setPreferredSize( new Dimension( StandardButton.FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
			setIcon( person.getIcon() );
			
			setFont( titleFont );
			setText( person.getName() );
			
			setActionCommand( "Select Person" );
			addActionListener( actionListener );
		}
		
		public Person getPerson(){ return person; }
	}
	
	//--------------------------------------------------------------------------
	protected JPanel buildCenterPanel(){
		//this center panel holds all the buttons mapped to all known persons 
		//	in the mainFrame's personCollection
		debugOutput( "PeopleOverviewPanel.buildCenterPanel()" );
		
		BorderLayout layout = new BorderLayout();
		layout.setVgap( STANDARD_GAP );
		JPanel thisPanel = new JPanel( layout );
		
		thisPanel.setBorder( createPaddedBorder() );

		//TODO: ??a little help?
//		pictureLabel = new JLabel( "Choose a person below to display their info" );
//		pictureLabel.setHorizontalAlignment( SwingConstants.CENTER );
//		pictureLabel.setFont( containedBy.labelFont );
//	  thisPanel.add( pictureLabel, BorderLayout.NORTH );
		
	  //add picture pane with icons (grid layout with 1 column)
		//TODO: ??scrollable?
	  peopleButtonPanel = new JPanel();
	  GridLayout iconLayout = 
	  		new GridLayout( containedBy.personCollection.size(), 1 );

	  //if there are no user's, display a bit of prompt
	  if( containedBy.personCollection.size() == 0 ){
	  	
	  	JLabel noPersonsLabel = new JLabel( 
  			"To add a person to this household, press the 'Add Person' button "
	  		+ "on the top right" );
			noPersonsLabel.setHorizontalAlignment( SwingConstants.CENTER );
			noPersonsLabel.setFont( labelFont );
			peopleButtonPanel.add( noPersonsLabel );
	  	
		//otherwise, build some person buttons for each person	
	  } else {
	  	
		  //TODO: could refine Hgap to make buttons square
		  iconLayout.setHgap( STANDARD_GAP * 2 );
		  peopleButtonPanel.setLayout( iconLayout );
		  
		  for( Person person : containedBy.personCollection.values() ){
		  	peopleButtonPanel.add( 
		  			new PersonButton( person, this ) );
		  }
	  }
	  thisPanel.add( peopleButtonPanel, BorderLayout.CENTER );
		  
		centerPanel = thisPanel;
		return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	//no bottom panel - return null
	protected JPanel buildBottomPanel(){ return null; }

	//--------------------------------------------------------------------------
	public void actionPerformed( ActionEvent e ){
		debugOutput( "PeopleOverviewPanel.actionPerfomed: " 
				+ e.getActionCommand() );
		
		//switch to the AddPersonPanel
		if ( e.getSource() == backButton ){
			containedBy.switchToContextPanel(contextPanel);
						
		//goto the specified person's PersonInfoPanel	
		} else if ( e.getActionCommand().equals( "Select Person" ) ){
			System.out.println( "source: " + e.getSource() );
			
			//get the person and switch
                        EventPanel panel = (EventPanel)contextPanel;
                        panel.actionPerformed(e);
                        
			containedBy.switchToContextPanel(contextPanel);
		}
	}
	
}
//==============================================================================
