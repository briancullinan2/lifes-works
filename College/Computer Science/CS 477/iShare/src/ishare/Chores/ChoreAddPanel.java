package ishare.Chores;

import ishare.Main.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Jarid Bredemeier
 * @version 2009.11.04
 */

public class ChoreAddPanel extends TitledPanel implements DocumentListener  {


    // Class Method(s)
    // Class Variable(s)
  //maximum selectable icons displayed in a row in the icon chooser
  protected final int ICON_BUTTONS_PER_COLUMN = 4;

  //the data needed to make a person
  String title = null;
  ImageIcon icon = null;
    
	//...........................................
  // chore name entry subpanel
  private JPanel titlePanel = null;
  private JLabel titleLabel = null;
  private JTextField titleTextField = null;
  private JButton titleKeyboardButton = null;
  
	//...........................................
  // chore name entry subpanel
  private JPanel descPanel = null;
  private JLabel descLabel = null;
  private JTextArea descTextArea = null;
  private JButton descKeyboardButton = null;
  
	//...........................................
  //subpanel displayed when the icon has been selected
  private JPanel pictureSelectedPanel = null;
  private JLabel chorePictureLabel = null;
  private JLabel chorePictureIcon = null;
  private JButton changeChorePictureButton = null;
  
	//...........................................
  //icon selection subpanel
  private JPanel pictureNotSelectedPanel = null;
  private JLabel pictureLabel = null;
  private JPanel iconPanel = null;

    // Class Constructor(s)
    public ChoreAddPanel( JFrame containedBy, JPanel contextPanel )
    {
        super(containedBy, contextPanel);

        build();

    }

    protected JPanel buildNavigationPanel()
    {
        //nav panel with forward button leading to AddPersonPanel
        //	(back button unwired)
        JPanel thisPanel = new JPanel( new BorderLayout() );
        debugOutput( "ChoresPanel.buildNavigationPanel()" );

        backButton = new StandardButton( "Cancel" );
        backButton.setActionCommand( "Back" );
        backButton.addActionListener( this );
        thisPanel.add( backButton, BorderLayout.WEST );

        titleLabel = new JLabel( "Add a New Chore" );
        titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
        titleLabel.setFont( titleFont );
        thisPanel.add( titleLabel, BorderLayout.CENTER );

        forwardButton = new StandardButton( "Add Chore" );
        forwardButton.setActionCommand( "Add Chore" );
        forwardButton.addActionListener( this );
        forwardButton.setEnabled(false);
        thisPanel.add( forwardButton, BorderLayout.EAST );

        navigationPanel = thisPanel;
        return thisPanel;
    }
    

    // Initialize Component(s)
    protected JPanel buildCenterPanel() {
        
        debugOutput( "ChoresPanel.buildCenterPanel()" );

        BorderLayout centerLayout = new BorderLayout();
        centerLayout.setVgap( STANDARD_GAP ); 
        centerPanel = new JPanel( centerLayout );

        //this panels center holds both the name entry ui and 
        //	icon selection stuff
        BorderLayout layout = new BorderLayout();
        layout.setVgap( STANDARD_GAP );
        JPanel toppanel = new JPanel(layout);
        
        toppanel.add(buildNameEntryPanel(), BorderLayout.NORTH);
        toppanel.add(buildDescriptionEntryPanel(), BorderLayout.CENTER);
        
        centerPanel.add( toppanel, BorderLayout.NORTH );
        centerPanel.add( buildPictureNotSelectedPanel(), BorderLayout.CENTER );
        add( centerPanel, BorderLayout.CENTER );

        return centerPanel;
    }

    //--------------------------------------------------------------------------
    private JPanel buildNameEntryPanel(){
        //consists of a help label, textfield, and keyboard button
        debugOutput( "AddPersonPanel.buildNameEntryPanel()" );

        BorderLayout layout = new BorderLayout();
        layout.setHgap( STANDARD_GAP );
        JPanel thisPanel = new JPanel( layout );

        thisPanel.setBorder( createPaddedBorder() );

        titleLabel = new JLabel( "Chore:" );
        titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
        titleLabel.setFont( labelFont );
        titleLabel.setPreferredSize(
                        new Dimension( StandardButton.FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
        thisPanel.add( titleLabel, BorderLayout.WEST );

        titleTextField = new TextHilight( "" );
        titleTextField.setFont( titleFont );
        titleTextField.setActionCommand( "Text Entered" );
        titleTextField.addActionListener( this );
        titleTextField.getDocument().addDocumentListener(this);
        //TODO: this needs better wiring for keypress events
        thisPanel.add( titleTextField, BorderLayout.CENTER );

        titleKeyboardButton = new StandardButton( "Open Keyboard" );
        titleKeyboardButton.setActionCommand( "Title Keyboard" );
        titleKeyboardButton.addActionListener( this );
        thisPanel.add( titleKeyboardButton, BorderLayout.EAST );

        titlePanel = thisPanel;
        return thisPanel;
    }

    private JPanel buildDescriptionEntryPanel()
    {
        //consists of a help label, textfield, and keyboard button
        debugOutput( "AddPersonPanel.buildDescriptionEntryPanel()" );

        BorderLayout layout = new BorderLayout();
        layout.setHgap( STANDARD_GAP );
        JPanel thisPanel = new JPanel( layout );

        thisPanel.setBorder( createPaddedBorder() );


        descLabel = new JLabel( "Description:" );
        descLabel.setHorizontalAlignment( SwingConstants.CENTER );
        descLabel.setFont( labelFont );
        descLabel.setPreferredSize(
                        new Dimension( StandardButton.FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
        thisPanel.add( descLabel, BorderLayout.WEST );

        descTextArea = new TextAreaHilight( "" );
        descTextArea.setFont( titleFont );
        descTextArea.setBorder(titleTextField.getBorder());
        descTextArea.setRows(3);
        //descTextArea.setActionCommand( "Text Entered" );
        //descTextArea.addActionListener( this );
        //TODO: this needs better wiring for keypress events
        thisPanel.add( descTextArea, BorderLayout.CENTER );

        descKeyboardButton = new JButton( "Open Keyboard" );
        descKeyboardButton.setActionCommand( "Desc Keyboard" );
        descKeyboardButton.addActionListener( this );
        thisPanel.add( descKeyboardButton, BorderLayout.EAST );


        descPanel = thisPanel;
        return thisPanel;
        
    }
    
    //--------------------------------------------------------------------------
    private JPanel buildPictureNotSelectedPanel(){
            //if the user hasn't selected an icon, display this panel
            //	contains a grid of buttons with the available person icons
            debugOutput( "AddPersonPanel.buildPictureNotSelectedPanel()" );

            BorderLayout layout = new BorderLayout();
            layout.setVgap( STANDARD_GAP );
            JPanel thisPanel = new JPanel( layout );

            thisPanel.setBorder( createPaddedBorder() );

            pictureLabel = new JLabel( "Choose a picture for this chore" );
            pictureLabel.setHorizontalAlignment( SwingConstants.CENTER );
            pictureLabel.setFont( labelFont );
      thisPanel.add( pictureLabel, BorderLayout.NORTH );

      //add picture scrollable pane with icons
      iconPanel = new JPanel();

      //set up the icon selection grid layout
      GridLayout iconLayout = null;
      int numOfAvailIcons = 
            containedBy.choreCollection.getNumberOfAvailableIcons();
      //if there are more than one row can display, add rows
      if( numOfAvailIcons > ICON_BUTTONS_PER_COLUMN ){
            iconLayout = new GridLayout( 
                           ICON_BUTTONS_PER_COLUMN , ( numOfAvailIcons / ICON_BUTTONS_PER_COLUMN ) );

      //otherwise one row or less
      } else {
            iconLayout = new GridLayout( 1, numOfAvailIcons );
      }
      //TODO: could refine Hgap to make buttons square
      iconLayout.setHgap( STANDARD_GAP * 2 );
      iconLayout.setVgap( STANDARD_GAP * 2 );
      iconPanel.setLayout( iconLayout );

      //add the icon selection buttons using the builder method below
      for( ImageIcon icon : containedBy.choreCollection.getAvailableIcons() ){
            iconPanel.add( buildChorePicButton( icon ) );
      }
      thisPanel.add( iconPanel, BorderLayout.CENTER );

            pictureNotSelectedPanel = thisPanel;
            return thisPanel;
    }

	//--------------------------------------------------------------------------
	public void switchToPictureSelected(){
		//switches the center subpanel to show the icon the user has
		//	selected
		debugOutput( "AddPersonPanel.switchToPictureSelected()" );

		centerPanel.remove( pictureNotSelectedPanel );
		centerPanel.add( buildPictureSelectedPanel(), BorderLayout.CENTER );
		containedBy.repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToPictureNotSelected(){
		//switches the center subpanel to show the icon selection subpanel
		debugOutput( "AddPersonPanel.switchToPictureSelected()" );

		centerPanel.remove( pictureSelectedPanel );
		centerPanel.add( buildPictureNotSelectedPanel(), BorderLayout.CENTER );
		containedBy.repack();
	}
	
	//--------------------------------------------------------------------------
	private JPanel buildPictureSelectedPanel(){
            //this is the subpanel that replaces the icon selection subpanel
            //	when the user selects an icon - it displays the icon chosen
            debugOutput( "AddPersonPanel.buildPictureSelectedPanel()" );

            BorderLayout layout = new BorderLayout();
            layout.setVgap( STANDARD_GAP );
            JPanel thisPanel = new JPanel( layout );

            thisPanel.setBorder( createPaddedBorder() );

            //TODO: HACK! - creates subpanel to prevent vertical stretching
            JPanel selectedPanel = new JPanel( new BorderLayout() );

            //prompt label (personalized)
            if( title != null ){
                    chorePictureLabel = new JLabel( title + "'s picture: " );

            } else {
                    chorePictureLabel = new JLabel( "Selected picture: " );
            }
            chorePictureLabel.setHorizontalAlignment( SwingConstants.CENTER );
            chorePictureLabel.setFont( labelFont );
            selectedPanel.add( chorePictureLabel, BorderLayout.WEST );
	  
                //a label with the selected icon
            chorePictureIcon = new JLabel();
            chorePictureIcon.setIcon( icon );
            selectedPanel.add( chorePictureIcon, BorderLayout.CENTER );

                //a button to reselect icon
            //TODO: doesn't handle long string well...
            changeChorePictureButton = new StandardButton( "Change Picture" );
            changeChorePictureButton.setActionCommand( "Reselect Picture" );
            changeChorePictureButton.addActionListener( this );
            selectedPanel.add( changeChorePictureButton, BorderLayout.EAST );

            thisPanel.add( selectedPanel, BorderLayout.NORTH );

            pictureSelectedPanel = thisPanel;
            return thisPanel;
	}
	
	
    //--------------------------------------------------------------------------
    private JButton buildChorePicButton( ImageIcon icon ){
            //builds an icon selection button
            debugOutput( "AddPersonPanel.buildChorePicButton(icon)" );
            JButton thisButton = new StandardButton("", icon);
            thisButton.setActionCommand( "Icon Selected" );
            thisButton.addActionListener( this );

            return thisButton;
    }

    protected JPanel buildBottomPanel(){ return null; }

    //--------------------------------------------------------------------------
    public void checkCompletedForm(){
      debugOutput( "AddPersonPanel.checkCompletedForm()" );

      //checks whether we have all the person data (currently only 2 items)
      //	and enables the add button if we do
      if( ( title != null && !titleTextField.getText().equals( "" ) ) && ( icon != null ) ){
              forwardButton.setEnabled( true );
              debugOutput( "AddChorePanel:   form complete. Enabling add button" );

      //otherwise, ensures add button disabled
      } else {
              forwardButton.setEnabled( false );
              debugOutput( "AddChorePanel:   form incomplete. Still missing: " 
                              + ( ( title == null )?( "name" ):( "" ) ) 
                              + ( ( icon == null )?( "icon" ):( "" ) ) );
      }

    }	

    public void actionPerformed(ActionEvent e) {
		debugOutput( "AddChorePanel.actionPerfomed: " 
				+ e.getActionCommand() );
		
			//go back to the people overview page (2 buttons call this)
			if( e.getActionCommand().equals( "Back" ) ){
				containedBy.switchToContextPanel(contextPanel);
				
				//user wants to reselect the icon
			} else if ( e.getActionCommand().equals( "Title Keyboard" ) ){
				debugOutput( "AddChorePanel:   Switching to keyboard for title entry" );
				
				containedBy.switchToKeyboardPanel( this, titleTextField );
				checkCompletedForm();
				
			} else if ( e.getActionCommand().equals( "Desc Keyboard" ) ){
				debugOutput( "AddChorePanel:   Switching to keyboard for desc entry" );
				
				containedBy.switchToKeyboardPanel( this, descTextArea );
				checkCompletedForm();
				
			//the user has selected an icon
			} else if ( e.getActionCommand().equals( "Icon Selected" ) ){
				debugOutput( "AddChorePanel:   An icon selector was pressed" );
				
				//get the item, check for form completion, 
				//	and display the selected icon
				JButton sourceButton = (JButton)e.getSource();
				icon = (ImageIcon)sourceButton.getIcon();
				checkCompletedForm();
				switchToPictureSelected();
				
			//user wants to reselect the icon
			} else if ( e.getActionCommand().equals( "Reselect Picture" ) ){
				debugOutput( "AddChorePanel:   Reselecting Picture" );
				
				//clear the icon, check the form, and show the icon selection panel
				icon = null;
				checkCompletedForm();
				switchToPictureNotSelected();
	
			//the user has clicked the add person button after completing the form
			} else if ( e.getActionCommand().equals( "Add Chore" ) ){
				debugOutput( "AddChorePanel:   Adding Chore" );
	
	                        //add the person to the main collection and switch back to overview
				containedBy.choreCollection.addChore( new Chore( title, descTextArea.getText(), icon ) );
				containedBy.switchToContextPanel(contextPanel);
			}
	        
    }
    
    public void changedUpdate(DocumentEvent e) {
        checkCompletedForm();
    }
    public void removeUpdate(DocumentEvent e) {
        checkCompletedForm();
    }
    public void insertUpdate(DocumentEvent e) {
        debugOutput( "AddChorePanel:   The name field has changed" );

        //clear the name, check the textfield contents to make sure
        //	it's not null, empty, or someone else's name
        title = null;
        String titleFieldText = titleTextField.getText();
        debugOutput( "AddChorePanel:   nameFieldText: " + titleFieldText );
        if( ( !titleFieldText.equals( "" ) ) &&
                        ( !containedBy.personCollection.containsKey( titleFieldText ) ) ){

                //set the name if it's good
                title = titleFieldText;
                debugOutput( "AddChorePanel:   name set to: " + title );
        }
        //TODO: need feedback to user about choosing someone else's name

        //if the selected icon panel exists, change the prompt label to 
        //	the new name (or generic if name is null)
        if( chorePictureLabel != null ){
                chorePictureLabel.setText( 
                                ( ( title != null )?
                                        ( title + "'s picture: " ):
                                        ( "Selected picture" ) ) );
        }

        checkCompletedForm();
    }

}
