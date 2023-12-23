//==============================================================================
package ishare.People;

import ishare.Main.Person;
import ishare.Main.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.event.*;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/*=========================================================================*//**
 * @author Carl Eberhard
 * @version 0.1
 * 
 *///===========================================================================
public class EditPersonPanel extends TitledPanel implements DocumentListener {

	//...........................................
	//we need a subclass cast of the containing frame to access the 
	//	personCollection and panel switching methods
  //maximum selectable icons displayed in a row in the icon chooser
  protected final int ICON_BUTTONS_PER_COLUMN = 6;

  //the data needed to make a person
  String name = null;
  ImageIcon icon = null;
  Person currentPerson = null;
  
	//...........................................
  //name entry subpanel
  private JPanel namePanel = null;
  private JLabel nameLabel = null;
  private JTextField nameTextField = null;
  private JButton nameKeyboardButton = null;
  
	//...........................................
  //icon selection subpanel
  private JPanel pictureNotSelectedPanel = null;
  private JLabel pictureLabel = null;
  private JPanel iconPanel = null;

	//...........................................
  //subpanel displayed when the icon has been selected
  private JPanel pictureSelectedPanel = null;
  private JLabel personsPictureLabel = null;
  private JLabel personsPictureIcon = null;
  private JButton changePersonsPictureButton = null;
  
	//...........................................
  //lower dialog-like panel (cancel and go back, add this user)
  private JPanel bottomPanel = null;
  private JButton cancelButton = null;
  
	//--------------------------------------------------------------------------
	public EditPersonPanel( 
			JFrame containedBy, JPanel contextPanel, Person person ){
		super( containedBy, contextPanel );
		debugOutput( "new EditPersonPanel(" + containedBy + ")" );
		
		name = person.getName();
		icon = person.getIcon();
		debugOutput( "--name = " + name + ", icon = " + icon );
		currentPerson = person;
		
		build();
		debugOutput( "--name = " + name + ", icon = " + icon );
		checkCompletedForm();
	}
	
	//--------------------------------------------------------------------------
	protected JPanel buildNavigationPanel()
        {
            debugOutput( "EditPersonPanel.buildNavigationPanel()" );

            JPanel thisPanel = new JPanel( new BorderLayout() );

            //back button which goes back to the overview panel
            if(backButton == null)
            {
                backButton = new StandardButton( "Cancel" );
                backButton.setActionCommand( "Back" );
                backButton.addActionListener( this );
                thisPanel.add( backButton, BorderLayout.WEST );
            }

            if(titleLabel == null)
            {
                titleLabel = new JLabel( "Edit " + name + "'s Data" );
                titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
                titleLabel.setFont( titleFont );
                thisPanel.add( titleLabel, BorderLayout.CENTER );
            }
            
            if(forwardButton == null)
            {
                forwardButton = new StandardButton( "Ok" );
                forwardButton.setActionCommand( "Accept" );
                forwardButton.addActionListener( this );
                thisPanel.add( forwardButton, BorderLayout.EAST );
            }
            
            navigationPanel = thisPanel;
            return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	protected JPanel buildCenterPanel(){
		debugOutput( "EditPersonPanel.buildCenterPanel()" );
		
		BorderLayout centerLayout = new BorderLayout();
		centerLayout.setVgap( STANDARD_GAP ); 
		centerPanel = new JPanel( centerLayout );

		//this panels center holds both the name entry ui and 
		//	icon selection stuff
		centerPanel.add( buildNameEntryPanel(), BorderLayout.NORTH );
		centerPanel.add( buildPictureSelectedPanel(), BorderLayout.CENTER );
		add( centerPanel, BorderLayout.CENTER );
		
		return centerPanel;
	}

	//--------------------------------------------------------------------------
	private JPanel buildNameEntryPanel(){
		//consists of a help label, textfield, and keyboard button
		debugOutput( "EditPersonPanel.buildNameEntryPanel()" );
		
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
		
		nameTextField = new TextHilight( "" );
		nameTextField.setFont( titleFont );
		nameTextField.setActionCommand( "Text Entered" );
		nameTextField.addActionListener( this );
    nameTextField.getDocument().addDocumentListener(this);
    nameTextField.setText( name );
		thisPanel.add( nameTextField, BorderLayout.CENTER );

		nameKeyboardButton = new StandardButton( "Open Keyboard" );
		nameKeyboardButton.setActionCommand( "Keyboard" );
		nameKeyboardButton.addActionListener( this );
		thisPanel.add( nameKeyboardButton, BorderLayout.EAST );

		namePanel = thisPanel;
		return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	private JPanel buildPictureNotSelectedPanel(){
            //if the user hasn't selected an icon, display this panel
            //	contains a grid of buttons with the available person icons
            debugOutput( "EditPersonPanel.buildPictureNotSelectedPanel()" );

            BorderLayout layout = new BorderLayout();
            layout.setVgap( STANDARD_GAP );
            JPanel thisPanel = new JPanel( layout );

            thisPanel.setBorder( createPaddedBorder() );

            pictureLabel = new JLabel( "Choose a picture for this person" );
            pictureLabel.setHorizontalAlignment( SwingConstants.CENTER );
            pictureLabel.setFont( labelFont );
            thisPanel.add( pictureLabel, BorderLayout.NORTH );

            //add picture scrollable pane with icons
            iconPanel = new JPanel();

            //set up the icon selection grid layout
            GridLayout iconLayout = null;
            int numOfAvailIcons = 
                containedBy.personCollection.getNumberOfAvailableIcons();
            //if there are more than one row can display, add rows
            if( numOfAvailIcons > ICON_BUTTONS_PER_COLUMN ){
                iconLayout = new GridLayout( ICON_BUTTONS_PER_COLUMN,
                   (int)Math.ceil( numOfAvailIcons / ICON_BUTTONS_PER_COLUMN ) );

            //otherwise one row or less
            } else {
                iconLayout = new GridLayout( 1, numOfAvailIcons );
            }
            //TODO: could refine Hgap to make buttons square
            iconLayout.setHgap( STANDARD_GAP * 2 );
            iconLayout.setVgap( STANDARD_GAP * 2 );
            iconPanel.setLayout( iconLayout );

            //add the icon selection buttons using the builder method below
            for( ImageIcon icon : containedBy.personCollection.getAvailableIcons() ){
                iconPanel.add( buildUserPicButton( icon ) );
            }
            iconPanel.add( buildUserPicButton( icon ) );
            thisPanel.add( iconPanel, BorderLayout.CENTER );

            pictureNotSelectedPanel = thisPanel;
            return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	private JButton buildUserPicButton( String picPath ){
		//creates an icon and sends it to the build method below
		debugOutput( "EditPersonPanel.buildUserPicButton(" + picPath + ")" );
		return buildUserPicButton( createImageIcon( picPath ) );
	}
	
	//--------------------------------------------------------------------------
	private JButton buildUserPicButton( ImageIcon icon ){
		//builds an icon selection button
		debugOutput( "EditPersonPanel.buildUserPicButton(icon)" );
		JButton thisButton = new StandardButton("", icon);
		thisButton.setActionCommand( "Icon Selected" );
		thisButton.addActionListener( this );
		
		return thisButton;
	}
	
	//--------------------------------------------------------------------------
	protected JPanel buildBottomPanel(){ return null; }

	//--------------------------------------------------------------------------
	public void switchToPictureSelected(){
		//switches the center subpanel to show the icon the user has
		//	selected
		debugOutput( "EditPersonPanel.switchToPictureSelected()" );

		centerPanel.remove( pictureNotSelectedPanel );
		centerPanel.add( buildPictureSelectedPanel(), BorderLayout.CENTER );
		containedBy.repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToPictureNotSelected(){
		//switches the center subpanel to show the icon selection subpanel
		debugOutput( "EditPersonPanel.switchToPictureSelected()" );

		centerPanel.remove( pictureSelectedPanel );
		centerPanel.add( buildPictureNotSelectedPanel(), BorderLayout.CENTER );
		containedBy.repack();
	}
	
	//--------------------------------------------------------------------------
	private JPanel buildPictureSelectedPanel(){
            //this is the subpanel that replaces the icon selection subpanel
            //	when the user selects an icon - it displays the icon chosen
            debugOutput( "EditPersonPanel.buildPictureSelectedPanel()" );

            BorderLayout layout = new BorderLayout();
            layout.setVgap( STANDARD_GAP );
            JPanel thisPanel = new JPanel( layout );

            thisPanel.setBorder( createPaddedBorder() );

            //TODO: HACK! - creates subpanel to prevent vertical stretching
            JPanel selectedPanel = new JPanel( new BorderLayout() );

            //prompt label (personalized)
            if( name != null ){
                    personsPictureLabel = new JLabel( name + "'s picture: " );

            } else {
                    personsPictureLabel = new JLabel( "Selected picture: " );
            }
            personsPictureLabel.setHorizontalAlignment( SwingConstants.CENTER );
            personsPictureLabel.setFont( labelFont );
            selectedPanel.add( personsPictureLabel, BorderLayout.WEST );
	  
                //a label with the selected icon
            personsPictureIcon = new JLabel();
            personsPictureIcon.setIcon( icon );
            selectedPanel.add( personsPictureIcon, BorderLayout.CENTER );

                //a button to reselect icon
            //TODO: doesn't handle long string well...
            changePersonsPictureButton = new StandardButton( "Change Picture" );
            changePersonsPictureButton.setActionCommand( "Reselect Picture" );
            changePersonsPictureButton.addActionListener( this );
            selectedPanel.add( changePersonsPictureButton, BorderLayout.EAST );

            thisPanel.add( selectedPanel, BorderLayout.NORTH );

            pictureSelectedPanel = thisPanel;
            return thisPanel;
	}
	
	//--------------------------------------------------------------------------
	public void checkCompletedForm(){
		debugOutput( "EditPersonPanel.checkCompletedForm()" );
		
		debugOutput( "--name = " + name + ", icon = " + icon );

                name = null;
                //clear the name, check the textfield contents to make sure
                //	it's not null, empty, or someone else's name
                String nameFieldText = nameTextField.getText();
                debugOutput( "EditPersonPanel:   nameFieldText: " + nameFieldText );
                if( ( !nameFieldText.equals( "" ) ) &&
                    ( !containedBy.personCollection.containsKey( nameFieldText ) ) ){

                        //set the name if it's good
                        name = nameFieldText;
                        debugOutput( "EditPersonPanel:   name set to: " + name );
                }
                //TODO: need feedback to user about choosing someone else's name

                //if the selected icon panel exists, change the prompt label to 
                //	the new name (or generic if name is null)
                if( personsPictureLabel != null ){
                        personsPictureLabel.setText( 
                                        ( ( name != null )?
                                                ( name + "'s picture: " ):
                                                ( "Selected picture" ) ) );
                }

		//checks whether we have all the person data (currently only 2 items)
		//	and enables the add button if we do
		if( ( !nameTextField.getText().equals( "" ) ) && ( icon != null ) ){
			forwardButton.setEnabled( true );
			debugOutput( "EditPersonPanel:   form complete. Enabling add button" );
			
		//otherwise, ensures add button disabled
		} else {
			forwardButton.setEnabled( false );
			debugOutput( "EditPersonPanel:   form incomplete. Still missing: " 
					+ ( ( name == null )?( "name" ):( "" ) ) 
					+ ( ( icon == null )?( "icon" ):( "" ) ) );
		}
		
	}	
		
	//--------------------------------------------------------------------------
	public void actionPerformed( ActionEvent e ){
		debugOutput( "EditPersonPanel.actionPerfomed: " 
				+ e.getActionCommand() );
		
		//go back to the people overview page (2 buttons call this)
		if( e.getActionCommand().equals( "Back" ) ){
			containedBy.switchToContextPanel(contextPanel);
			
		//user wants to reselect the icon
		} else if ( e.getActionCommand().equals( "Keyboard" ) ){
			debugOutput( "EditPersonPanel: starting keyboard" );
			
			containedBy.switchToKeyboardPanel( this, nameTextField );
			checkCompletedForm();

		//the user has selected an icon
		} else if ( e.getActionCommand().equals( "Icon Selected" ) ){
			debugOutput( "EditPersonPanel:   An icon selector was pressed" );
			
			//get the item, check for form completion, 
			//	and display the selected icon
			JButton sourceButton = (JButton)e.getSource();
			icon = (ImageIcon)sourceButton.getIcon();
			checkCompletedForm();
			switchToPictureSelected();
			
			//user wants to reselect the icon
		} else if ( e.getActionCommand().equals( "Reselect Picture" ) ){
			debugOutput( "EditPersonPanel:   Reselecting Picture" );
			
			//clear the icon, check the form, and show the icon selection panel
			checkCompletedForm();
			switchToPictureNotSelected();

		//the user has clicked the add person button after completing the form
		} else if ( e.getActionCommand().equals( "Accept" ) ){
			debugOutput( "EditPersonPanel:   Changing person's data" );
			
			debugOutput( "name = " + name + ", icon = " + icon );
			containedBy.personCollection.remove(currentPerson.getName());
			currentPerson.setName( name );
			currentPerson.setIcon( icon );
                        containedBy.personCollection.addPerson(currentPerson);
			containedBy.switchToContextPanel(contextPanel);
		}
	}
        
	//--------------------------------------------------------------------------
    public void changedUpdate(DocumentEvent e) {
        checkCompletedForm();
    }
  	//--------------------------------------------------------------------------
    public void removeUpdate(DocumentEvent e) {
        checkCompletedForm();
    }
  	//--------------------------------------------------------------------------
    public void insertUpdate(DocumentEvent e) {
        debugOutput( "EditPersonPanel:   The name field has changed" );

        checkCompletedForm();
    }

}
//==============================================================================
