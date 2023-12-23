package ishare.Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.*;

import javax.swing.*;

/*=========================================================================*//**
 * @author Carl Eberhard
 * @version 0.1
 * 
 * Abstract superclass of PeopleOverviewPanel, AddPersonPanel,
 * 	and PersonInfoPanel.
 * Mainly a means to factor out common crap, constants, and particular styles
 * 	for UI elements (like common border, button, font styles).
 * Has another instance of the createImageIcon helper method.
 * Assumes all subclasses will have a navigation panel.
 * 
 * 
 *///===========================================================================
public abstract class TitledPanel extends JPanel implements ActionListener {

	//...........................................

	//used in hGap, vGap, and emptyBorder creation
	protected final int STANDARD_GAP = 8;
	protected final int BORDER_PADDING_SIZE = 16;
	
	//...........................................
	//common fonts
  protected Font titleFont = new Font( "SansSerif", Font.BOLD, 36 );
  protected Font labelFont = new Font( "SansSerif", Font.BOLD, 14 );
  protected Font smallFont = new Font( "SansSerif", Font.BOLD, 10 );
	
	//...........................................
        //the frame that will contain these (! assumes these are contentPanes)
	public TestFrame containedBy = null;
        
        // the panel that called the current panel to be viewed
        public TitledPanel contextPanel = null;
	
	//...........................................
	//common top panel holding a back button, a 'page' title,
	//	and (sometimes) a forward button
  protected JPanel navigationPanel = null;
  protected JButton backButton = null;
  protected JLabel titleLabel = null;
  protected JButton forwardButton = null;
  
	//...........................................
  //optional center and bottom panels
  protected JPanel centerPanel = null;
  protected JPanel bottomPanel = null;
	
	//--------------------------------------------------------------------------
	public TitledPanel( JFrame containedBy, JPanel contextPanel ){
		//really basic constructor 
		super( new BorderLayout() );
		debugOutput( "new TitledPanel(" + containedBy + ")" );
		
		this.containedBy = (TestFrame)containedBy;
		this.contextPanel = (TitledPanel)contextPanel;
	}
        
	//--------------------------------------------------------------------------
	public void build(){	
		//actual construction and assembly of panel happens here
		//placing this outside the constructor allows subclass vars to be
		//	set and used before building
		debugOutput( "TitledPanel: build()" );

		//setup the panel's layout (!note: assumes borderLayout)
		//all internal components will have STANDARD_GAP # of pixels (h & v)
		BorderLayout layout = ( BorderLayout )getLayout();
		layout.setHgap( STANDARD_GAP );
		layout.setVgap( STANDARD_GAP );
		setLayout( layout );
		
		//build and set the subpanels, including:
		//the navigation panel
		navigationPanel = buildNavigationPanel();
		if( navigationPanel != null ){
			add( navigationPanel, BorderLayout.NORTH );
		}
		debugOutput( "TitledPanel nav panel built" );

		//the optional center panel (custom content)
		centerPanel = buildCenterPanel();
		if( centerPanel != null ){
			add( centerPanel, BorderLayout.CENTER );
		}
		
		//more optional custom content and dialog like options
		bottomPanel = buildBottomPanel();
		if( bottomPanel != null ){
			add( bottomPanel, BorderLayout.SOUTH );
		}

		//set up a padded border around the entire panel
		setBorder( new EmptyBorder( 
				BORDER_PADDING_SIZE, BORDER_PADDING_SIZE, 
				BORDER_PADDING_SIZE, BORDER_PADDING_SIZE ) ); 
	}
	
        public void updatePanel()
        {
            updateNavigationPanel();
            
            updateCenterPanel();
        }
        
        public void updateNavigationPanel()
        {
            if( navigationPanel != null ){
                this.remove(navigationPanel);
            }
            //the optional center panel (custom content)
            navigationPanel = buildNavigationPanel();
            if( navigationPanel != null ){
                    add( navigationPanel, BorderLayout.NORTH );
            }
        }

        public void updateCenterPanel()
        {
            if( centerPanel != null ){
                this.remove(centerPanel);
            }
            //the optional center panel (custom content)
            centerPanel = buildCenterPanel();
            if( centerPanel != null ){
                    add( centerPanel, BorderLayout.CENTER );
            }
        }

	//--------------------------------------------------------------------------
	//These are the panel content methods that are meant to be overridden by 
	//	any subclasses
	abstract protected JPanel buildNavigationPanel();
	
	//--------------------------------------------------------------------------
	abstract protected JPanel buildCenterPanel();
	
	//--------------------------------------------------------------------------
	abstract protected JPanel buildBottomPanel();
        
	//--------------------------------------------------------------------------
	protected CompoundBorder createPaddedBorder(){
		debugOutput( "TitledPanel: createPaddedBorder()" );
		
		//factory method to create the padded borders that surround 
		//	most inner components
		Border lineBorder = BorderFactory.createLineBorder( Color.black );
		EmptyBorder emptyBorder = new EmptyBorder( 8, 8, 8, 8 );
		return ( new CompoundBorder( lineBorder, emptyBorder ) );
	}

	//--------------------------------------------------------------------------
	protected ImageIcon createImageIcon( String path ){
            debugOutput( "TitledPanel: createImageIcon(" + path + ")" );
    
            java.net.URL imgURL = ishare.Main.TestFrame.class.getResource(path);
            if( imgURL == null ){
                System.err.println( "Couldn't find file: " + path );
                return null;
            }
            return new ImageIcon( imgURL );
	}
	
	//--------------------------------------------------------------------------
	//debugging field and method - set debuggingOn to false to suppress msgs
	protected static boolean debuggingOn = true;
	protected static void debugOutput( String msg ){
		if( debuggingOn ){
			System.out.println( "debug: " + msg );
		}
	}
}
