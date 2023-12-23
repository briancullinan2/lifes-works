package ishare.Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

public class TouchKeyboard extends TitledPanel {

	protected final int FINGER_WIDTH = 68;
	protected final int FINGER_HEIGHT = 64;

	protected String enteredText = "";
	protected JTextComponent textComponent = null;
	
	JTextArea entryTextArea = null;
	HashMap< String, KeyboardKeyButton > keys = null;
	
	boolean capsOn = false;
	JButton capsButton = null;
	
	String[] lowerKeyValues = {
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "Bksp",
			"Tab", "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
			"a", "s", "d", "f", "g", "h", "j", "k", "l", ":", "Enter",
			"z", "x", "c", "v", "b", "n", "m", ",", ".", "?", "-"
	};
	String[] upperKeyValues = {
			"!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "Bksp",
			"Tab", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
			"A", "S", "D", "F", "G", "H", "J", "K", "L", ":", "Enter",
			"Z", "X", "C", "V", "B", "N", "M", ",", ".", "?", "+"
	};
	
	//--------------------------------------------------------------------------
	public TouchKeyboard( JFrame containedBy, JPanel contextPanel, 
			JTextComponent textComponent ){
		super( containedBy, contextPanel );
//		debuggingOn = false;
		debugOutput( "new TouchKeyboard(" + containedBy + ")" );
		
		keys = new HashMap< String, KeyboardKeyButton >();
		this.textComponent = textComponent;
		
		build();
		
		enteredText = textComponent.getText();
		entryTextArea.setText( enteredText );
		entryTextArea.requestFocusInWindow();
	}

	//--------------------------------------------------------------------------
	protected JPanel buildNavigationPanel(){
    debugOutput( "TouchKeyboard.buildNavigationPanel()" );

    BorderLayout layout = new BorderLayout();
    layout.setHgap( STANDARD_GAP );
    JPanel thisPanel = new JPanel( layout );
    
    //back button which goes back to the overview panel
    backButton = new StandardButton( "Back" );
    backButton.setActionCommand( "Back" );
    backButton.addActionListener( this );
    thisPanel.add( backButton, BorderLayout.WEST );

    titleLabel = new JLabel( "Enter Your Text" );
    titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
    titleLabel.setFont( titleFont );
    thisPanel.add( titleLabel, BorderLayout.CENTER );

    forwardButton = new StandardButton( "Ok" );
    forwardButton.setActionCommand( "Ok" );
    forwardButton.setEnabled(true);
    forwardButton.addActionListener( this );
    thisPanel.add( forwardButton, BorderLayout.EAST );

    navigationPanel = thisPanel;
    return thisPanel;
	}

	//--------------------------------------------------------------------------
	protected JPanel buildCenterPanel(){
		
		BorderLayout layout = new BorderLayout();
		layout.setVgap( STANDARD_GAP );
		centerPanel = new JPanel( layout );
		
		entryTextArea = new JTextArea( 3, 24 );
		entryTextArea.setTabSize( 2 );
		entryTextArea.setFont( titleFont );
		entryTextArea.setPreferredSize( 
				new Dimension( containedBy.getWidth(), 240 ) );
		entryTextArea.setBorder( BorderFactory.createLineBorder( Color.black ) );
		
    centerPanel.add( entryTextArea, BorderLayout.NORTH );

    centerPanel.add( buildKeyboardPanel(), BorderLayout.CENTER );
    
		return centerPanel;
	}
	
	//--------------------------------------------------------------------------
	class KeyboardKeyButton extends JButton {
		
		protected String lowerKeyValue = null;
		protected String upperKeyValue = null;
		
		public KeyboardKeyButton( String lowerKeyValue, String upperKeyValue ){
			super( lowerKeyValue );
			
			setPreferredSize( new Dimension( FINGER_WIDTH, FINGER_HEIGHT ) );
			setFont( labelFont );

			setActionCommand( lowerKeyValue );
			
			this.lowerKeyValue = lowerKeyValue;
			this.upperKeyValue = upperKeyValue;
		}
		
		protected void setToUpper(){
			setText( upperKeyValue );
		}
		protected void setToLower(){
			setText( lowerKeyValue );
		}
	}
	
	
	//--------------------------------------------------------------------------
	protected JPanel buildKeyboardPanel(){
		
		BorderLayout mainLayout = new BorderLayout();
		mainLayout.setVgap( STANDARD_GAP );
		JPanel thisPanel = new JPanel( mainLayout );
		
		thisPanel.setBorder( createPaddedBorder() );
		
		GridLayout textLayout = new GridLayout( 4, 11 );
		textLayout.setHgap( STANDARD_GAP );
		textLayout.setVgap( STANDARD_GAP );
		JPanel keyPanel = new JPanel();
		
		KeyboardKeyButton key = null;
		for( int k = 0; k < lowerKeyValues.length; k++ ){
			key = new KeyboardKeyButton( lowerKeyValues[ k ], upperKeyValues[ k ] );
			key.addActionListener( this );
			keyPanel.add( key );
			keys.put( lowerKeyValues[ k ], key );
		}
		thisPanel.add( keyPanel, BorderLayout.CENTER );
		keys.get( "Bksp" ).setFont( smallFont );
		keys.get( "Enter" ).setFont( smallFont );
		
		//5: shift/caps,space,left,up,down,right
		BorderLayout controlLayout = new BorderLayout();
		controlLayout.setHgap( STANDARD_GAP );
		JPanel controlPanel = new JPanel( controlLayout );
		
		capsButton = new StandardButton( "Caps" );
		capsButton.setPreferredSize( 
				new Dimension( StandardButton.FINGER_WIDTH * 2, StandardButton.FINGER_HEIGHT ) );
		capsButton.setFont( labelFont );
		capsButton.setActionCommand( "caps" );
		capsButton.addActionListener( this );
		controlPanel.add( capsButton, BorderLayout.WEST );
		
		JButton spaceButton = new JButton();
		spaceButton.setPreferredSize( 
				new Dimension( spaceButton.getWidth(), StandardButton.FINGER_HEIGHT ) );
		spaceButton.setActionCommand( "space" );
		spaceButton.addActionListener( this );
		controlPanel.add( spaceButton, BorderLayout.CENTER );
		
		GridLayout positionLayout = new GridLayout( 1, 2 );//4 );
		positionLayout.setHgap( STANDARD_GAP );
		JPanel positionPanel = new JPanel( positionLayout );
		
		JButton leftButton = new JButton( "Left" );
		leftButton.setPreferredSize( 
				new Dimension( FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
		leftButton.setFont( smallFont );
		leftButton.setActionCommand( "left" );
		leftButton.addActionListener( this );
		positionPanel.add( leftButton );
		
//		JButton downButton = new JButton( "Down" );
//		downButton.setPreferredSize( 
//				new Dimension( FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
//		downButton.setFont( smallFont );
//		downButton.setActionCommand( "down" );
//		downButton.addActionListener( this );
//		positionPanel.add( downButton );
		
//		JButton upButton = new JButton( "Up" );
//		upButton.setPreferredSize( 
//				new Dimension( FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
//		upButton.setFont( smallFont );
//		upButton.setActionCommand( "up" );
//		upButton.addActionListener( this );
//		positionPanel.add( upButton );

		JButton rightButton = new JButton( "Right" );
		rightButton.setPreferredSize( 
				new Dimension( FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
		rightButton.setFont( smallFont );
		rightButton.setActionCommand( "right" );
		rightButton.addActionListener( this );
		positionPanel.add( rightButton );
		
		controlPanel.add( positionPanel, BorderLayout.EAST );
		thisPanel.add( controlPanel, BorderLayout.SOUTH );
		
		return thisPanel;
	}
	
	

	//--------------------------------------------------------------------------
	protected JPanel buildBottomPanel(){
		
//		GridLayout layout = new GridLayout( 1, 2 );
//		layout.setHgap( STANDARD_GAP );
//		JPanel thisPanel = new JPanel( layout );
//		
//		thisPanel.setBorder( createPaddedBorder() );
//
//		JButton cancelButton = new JButton( "Back" );
//		cancelButton.setPreferredSize( 
//				new Dimension( FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
//		cancelButton.setActionCommand( "back" );
//		cancelButton.addActionListener( this );
//		thisPanel.add( cancelButton );
//		
//		JButton acceptButton = new JButton( "Ok" );
//		acceptButton.setPreferredSize( 
//				new Dimension( FINGER_WIDTH, StandardButton.FINGER_HEIGHT ) );
//		acceptButton.setActionCommand( "accept" );
//		acceptButton.addActionListener( this );
//		thisPanel.add( acceptButton );
//		
//		
//		
//		bottomPanel = thisPanel;
//		return thisPanel;
		return null;
	}

	//--------------------------------------------------------------------------
	public void actionPerformed( ActionEvent e ){
		debugOutput( "TouchKeyboard.actionPerfomed: " 
				+ e.getActionCommand() );
		
		//go back to the people overview page (2 buttons call this)
		if( e.getActionCommand().equalsIgnoreCase( "Back" ) ){
			containedBy.switchToContextPanel(contextPanel);
		
		} else if( e.getActionCommand().equalsIgnoreCase( "Ok" ) ){
			textComponent.setText( enteredText );
			containedBy.switchToContextPanel(contextPanel);
			
		} else if( e.getActionCommand().equalsIgnoreCase( "bksp" ) ){
			deleteText();
			
		} else if( e.getActionCommand().equalsIgnoreCase( "tab" ) ){
			enterText( "\t" );
			
		} else if( e.getActionCommand().equalsIgnoreCase( "enter" ) ){
			enterText( "\n" );
			
		} else if( e.getActionCommand().equalsIgnoreCase( "space" ) ){
			enterText( " " );
			
		} else if( e.getActionCommand().equalsIgnoreCase( "caps" ) ){
			capsOn = !capsOn;
			if( capsOn ){
				capsButton.setBackground( Color.black );
				capsButton.setForeground( Color.white );
				capsButton.setText( "Caps on" );
				for( KeyboardKeyButton key : keys.values() ){
					key.setToUpper();
				}
				
			} else {
				capsButton.setBackground( this.getBackground() );
				capsButton.setForeground( this.getForeground() );
				capsButton.setText( "Caps" );
				for( KeyboardKeyButton key : keys.values() ){
					key.setToLower();
				}
			}
			
			
		} else if( e.getActionCommand().equalsIgnoreCase( "left" ) ){
			int pos = entryTextArea.getCaretPosition();
			if( pos > 0 ){
				entryTextArea.setCaretPosition( pos - 1 );
			}
			
		} else if( e.getActionCommand().equalsIgnoreCase( "right" ) ){
			int pos = entryTextArea.getCaretPosition();
			if( pos < entryTextArea.getDocument().getLength() ){
				entryTextArea.setCaretPosition( pos + 1 );
			}
			
		} else if( e.getActionCommand().equalsIgnoreCase( "up" ) ){
			int pos = entryTextArea.getCaretPosition();
			if( ( pos - 24 ) >= 0 ){
				entryTextArea.setCaretPosition( pos - 24 );
			} else {
				entryTextArea.setCaretPosition( 0 );
			}
			
		} else if( e.getActionCommand().equalsIgnoreCase( "down" ) ){
			int pos = entryTextArea.getCaretPosition();
			if( ( pos + 24 ) < entryTextArea.getDocument().getLength() ){
				entryTextArea.setCaretPosition( pos + 24 );
			} else {
				entryTextArea.setCaretPosition( 
						entryTextArea.getDocument().getLength() );
			}
			
		} else {
			
			KeyboardKeyButton sourceButton = keys.get( e.getActionCommand() );
			if( sourceButton == e.getSource() ){
				
				if( capsOn ){
					enterText( sourceButton.upperKeyValue );
					
				} else {
					enterText( sourceButton.lowerKeyValue );
					
				}
			}
		}
		
		entryTextArea.requestFocusInWindow();
	}

	//--------------------------------------------------------------------------
	public void deleteText(){
		
		if( entryTextArea.getSelectedText() != null ){
			entryTextArea.cut();
			enteredText = entryTextArea.getText();
			return;
		}
		
		int pos = entryTextArea.getCaretPosition();
		if( pos == 0 ){ return; }
		
		if( pos == entryTextArea.getDocument().getLength() ){
			enteredText = enteredText.substring( 0, enteredText.length() - 1 );
		
		} else {
			String firstPart = entryTextArea.getText().substring( 0, pos - 1 );
			String secondPart = entryTextArea.getText().substring( pos );
			enteredText = firstPart + secondPart;
			
		}	
		entryTextArea.setText( enteredText );
		if( pos > 0 ){
			entryTextArea.setCaretPosition( pos - 1 );
		}
	}
	
	
	//--------------------------------------------------------------------------
	public void enterText( String addedStr ){
	
		if( entryTextArea.getSelectedText() != null ){
			debugOutput( "selected text = " + entryTextArea.getSelectedText() );
			entryTextArea.cut();
			enteredText = entryTextArea.getText();
		} else {
			debugOutput( "null entryTextArea selection");
		}
		
		int pos = entryTextArea.getCaretPosition();
		debugOutput( "position = " + pos );
		
		if( pos == entryTextArea.getDocument().getLength() ){
			enteredText += addedStr;
		
		} else {
			String firstPart = entryTextArea.getText().substring( 0, pos );
			String secondPart = entryTextArea.getText().substring( pos );
			enteredText = firstPart + addedStr + secondPart;
			
		}	
		entryTextArea.setText( enteredText );
		entryTextArea.setCaretPosition( pos + addedStr.length() );
	}
}
