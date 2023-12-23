package ishare.Chores;
import ishare.Main.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Jarid Bredemeier
 * @version 2009.11.04
 */

//public class DivvyChoreCore extends JFrame {
public class ChoresPanel extends TitledPanel {

    // Class Variable(s)
    private JButton add;
    private JButton remove;
    private JButton edit;
	
  private JPanel choreButtonPanel = null;
    // Class Constructor(s)
//  public DivvyChoreCore(int width, int height, String title, Color bgColor) {
    public ChoresPanel( JFrame containedBy, JPanel contextPanel )
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

        backButton = new StandardButton( "Back" );
        backButton.addActionListener( this );
        thisPanel.add( backButton, BorderLayout.WEST );

        titleLabel = new JLabel( "Chores" );
        titleLabel.setHorizontalAlignment( SwingConstants.CENTER );
        titleLabel.setFont( titleFont );
        thisPanel.add( titleLabel, BorderLayout.CENTER );

        forwardButton = new StandardButton( "Add Chore" );
        forwardButton.addActionListener( this );
        thisPanel.add( forwardButton, BorderLayout.EAST );

        navigationPanel = thisPanel;
        return thisPanel;
    }
    
    protected JPanel buildCenterPanel(){
        //this center panel holds all the buttons mapped to all known persons 
        //	in the mainFrame's personCollection
        debugOutput( "ChoresPanel.buildCenterPanel()" );

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
	  choreButtonPanel = new JPanel();
	  GridLayout iconLayout = 
	  		new GridLayout( containedBy.personCollection.size(), 1 );

	  //if there are no user's, display a bit of prompt
	  if( containedBy.personCollection.size() == 0 ){
	  	
	  	JLabel noPersonsLabel = new JLabel( 
  			"To add a person to this household, press the 'Add Person' button "
	  		+ "on the top right" );
			noPersonsLabel.setHorizontalAlignment( SwingConstants.CENTER );
			noPersonsLabel.setFont( labelFont );
			choreButtonPanel.add( noPersonsLabel );
	  	
		//otherwise, build some person buttons for each person	
	  } else {

            //TODO: could refine Hgap to make buttons square
            iconLayout.setHgap( STANDARD_GAP * 2 );
            choreButtonPanel.setLayout( iconLayout );

            for( Chore chore : containedBy.choreCollection.values() ){
                JButton chorebtn = new JButton(chore.getTitle(), chore.getIcon());
                chorebtn.setFont( titleFont );
                chorebtn.setActionCommand( "Edit Chore" );
                chorebtn.addActionListener( this );
                choreButtonPanel.add(chorebtn);
            }
	  }
	  thisPanel.add( choreButtonPanel, BorderLayout.CENTER );
		  
		centerPanel = thisPanel;
		return thisPanel;
    }
    
    protected JPanel buildBottomPanel(){
        
        debugOutput( "ChoresPanel.buildCenterPanel()" );

        //BorderLayout layout = new BorderLayout();
        //layout.setVgap( STANDARD_GAP );
        JPanel thisPanel = new JPanel();
        
        add = new StandardButton("Add Chore");
        add.addActionListener(this);
        remove = new StandardButton("Remove Chore");
        remove.addActionListener(this);
        edit = new StandardButton("Edit Chore");
        edit.addActionListener(this);
        
        thisPanel.add(add);
        thisPanel.add(remove);
        thisPanel.add(edit);
        
        bottomPanel = thisPanel;
        return thisPanel;
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == backButton)
        {
            containedBy.switchToContextPanel(contextPanel);
        }
        else if(e.getSource() == forwardButton  || e.getSource() == add )
        {
            containedBy.switchToChoreAddPanel(this);
        }
        else if(e.getSource() == edit)
        {
            containedBy.switchToChoreSelectPanel(this, "Edit");
        }
        else if(e.getSource() == remove)
        {
            containedBy.switchToChoreSelectPanel(this, "Remove");
        }
        else if( e.getActionCommand().equalsIgnoreCase("Edit Chore") )
        {
            contextPanel.actionPerformed(e);

            containedBy.switchToChoreEditPanel(this, containedBy.choreCollection.get( ((JButton)e.getSource()).getText() ));
        }



        //switchToChoreRemovePanel
        
    }
    
    // Class Method(s)
  
}