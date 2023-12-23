//==============================================================================
package ishare.Main;

import ishare.Calendar.CalendarPanel;
import ishare.Calendar.CalendarView;
import ishare.Calendar.EventPanel;
import ishare.Chores.ChoreAddPanel;
import ishare.Chores.ChoresPanel;
import ishare.Chores.ChoresSelectPanel;
import ishare.Chores.ChoresEditPanel;
import ishare.People.AddPersonPanel;
import ishare.People.EditPersonPanel;
import ishare.People.PeoplePanel;
import ishare.People.PeopleSelectPanel;
import ishare.People.PersonInfoPanel;
import ishare.Resources.IconHashMap;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

/*=========================================================================*//**
 * @author Carl Eberhard
                                                                              * 
 * @version 0.1
 * 
 * Test frame for the Person handling ui panels I'm working on for the iShare
 * project (CS 477 - spring 09)
 * 
 * Mainly contains the PersonCollection that the AddPersonPanel, 
 * PeopleOverviewPanel, and PersonInfoPanel operate on.
 * 
 * Contains methods to switch from panel to panel (often called by a subpanel)
 * Contains the main method.
 * 
 * Begins by loading the PeopleOverPanel.
 * 
 * NOTE: currently uses non-12" height (my laptop won't do 864 height)
 * 
 *///===========================================================================
public class TestFrame extends JFrame {

	//frame dimensions: 12" x 72dpi = 864
	protected static final int FRAME_WIDTH = 864;
//	protected static final int FRAME_HEIGHT = 864;
	protected static final int FRAME_HEIGHT = 864; //864;

	//where the overall list of people (users) is held
	public PersonCollection personCollection = null;
	public ChoreCollection choreCollection = null;
    
        public Vector<Event> events = new Vector<Event>();
	
	public IconHashMap iconHashMap = new IconHashMap();
        
        public int last_month;
        public int last_year;
        public int last_day;
        public CalendarView.Layout last_layout;
        
        //public Container last_panel;
	
	//--------------------------------------------------------------------------
	public TestFrame(){
		super( "iShare TestFrame" );
		debugOutput( "new TestFrame()" );
		
                // set up last calendar view to current day
                Calendar calendar = Calendar.getInstance();
                last_year = calendar.get(Calendar.YEAR);
                last_month = calendar.get(Calendar.MONTH);
                last_day = calendar.get(Calendar.DAY_OF_MONTH);
                last_layout = CalendarView.Layout.MONTH;
                
		//set up the personCollection and put some peeps in it if debugging
		personCollection = new PersonCollection( iconHashMap );
		choreCollection = new ChoreCollection( iconHashMap );
		if( debuggingOn ){
			initPersonCollection();
			initChoreCollection();
			initEventCollection();
		}
		
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		//next op will pack the frame and set visible - we don't need to here
		switchToCalendarViewPanel( null );
	}
	
	//--------------------------------------------------------------------------
	protected void initPersonCollection(){
		//sets up a dummy list of test persons

		String[] testPersons = {
			"Fletcher Mackel",
			"Crystal Wicker", 
			"Darth Trevor", 
			"MC BigWheel"
//			, 
//			"Dick Army", 
//			"Your not funny, Carl"
		};

		for( String name : testPersons ){
                    Person person = new Person( name, personCollection.getNextAvailableIcon() );
			personCollection.addPerson( person );
		}
                
	}
        
	protected void initChoreCollection(){
		//sets up a dummy list of test persons

		Chore[] testChores = {
			new Chore("Laundry", "Yada Yada", iconHashMap.createImageIcon("date64.png")),
			new Chore("Dishes", "Yada Yada", iconHashMap.createImageIcon("date64.png")),
			new Chore("Homework", "Yada Yada", iconHashMap.createImageIcon("date64.png")),
//			, 
//			"Dick Army", 
//			"Your not funny, Carl"
		};

		for( Chore chore : testChores ){
			choreCollection.addChore( chore );
		}
	}
        
        protected void initEventCollection()
        {
                Event event = new Event(2009, 4, 1, personCollection.get("Crystal Wicker"), choreCollection.get("Dishes"));
                events.add(event);
                event = new Event(2009, 5, 17, personCollection.get("Crystal Wicker"), choreCollection.get("Dishes"));
                events.add(event);
                            
        }
        
        public ArrayList<Event> getEvents(Person person)
        {
            ArrayList<Event> return_val = new ArrayList<Event>();

            for(int i = 0; i < events.size(); i++)
            {
                Event e = events.get(i);
                if(e.person == person)
                {
                    return_val.add(e);
                }
            }

            return return_val;
        }
        
        public ArrayList<Event> getEvents(int year, int month, int day)
        {
            ArrayList<Event> return_val = new ArrayList<Event>();

            for(int i = 0; i < events.size(); i++)
            {
                Event e = events.get(i);
                if(e.year == year && e.month == month && e.day == day)
                {
                    return_val.add(e);
                }
            }

            return return_val;
        }
        
	//--------------------------------------------------------------------------
	public void repack(){
		//resets the dimensions and repaints by calling setVisible
		//	called by all panel switching methods below
		//	(had to add this to prevent frame shrinkage)
            debugOutput( "repack()" );

            setExtendedState(JFrame.MAXIMIZED_BOTH);
            //setSize( FRAME_WIDTH, FRAME_HEIGHT );
            setVisible( true );
	}
		
	//--------------------------------------------------------------------------
	public void switchToContextPanel(JPanel contextPanel){
            debugOutput( "switchToContextPanel()" );
            if(contextPanel instanceof TitledPanel)
                ((TitledPanel)contextPanel).updatePanel();
            setContentPane(contextPanel);
            repack();
        }
        
	//--------------------------------------------------------------------------
	public void switchToPreviousPanel(JPanel contextPanel){
            debugOutput( "switchToPreviousPanel()" );
            
            //setContentPane(last_panel);
            repack();
        }
		
	//--------------------------------------------------------------------------
	public void switchToCalendarViewPanel(JPanel contextPanel){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToCalendarViewPanel()" );
		
		setContentPane( new CalendarPanel( this, contextPanel, last_layout, last_year, last_month, last_day ) );
		repack();
	}
		
	//--------------------------------------------------------------------------
	public void switchToCalendarSelectPanel(JPanel contextPanel, int year, int month, int day){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToCalendarSelectPanel()" );
		
		setContentPane( new CalendarPanel( this, contextPanel, CalendarView.Layout.SELECT, year, month, day ) );
		repack();
	}
        
	//--------------------------------------------------------------------------
	public void switchToMonthViewPanel(JPanel contextPanel, int year, int month, int day){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToMonthViewPanel()" );
                
                last_year = year;
                last_month = month;
                last_day = day;
                last_layout = CalendarView.Layout.MONTH;
		
		setContentPane( new CalendarPanel( this, contextPanel, CalendarView.Layout.MONTH, year, month, day ) );
		repack();
	}
        
 	//--------------------------------------------------------------------------
	public void switchToDayViewPanel(JPanel contextPanel, int year, int month, int day){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToDayViewPanel()" );
                
                last_year = year;
                last_month = month;
                last_day = day;
                last_layout = CalendarView.Layout.DAY;
		
		setContentPane( new CalendarPanel( this, contextPanel, CalendarView.Layout.DAY, year, month, day ) );
		repack();
	}
       
	//--------------------------------------------------------------------------
	public void switchToEventPanel(JPanel contextPanel){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToEventPanel()" );
		
		setContentPane( new EventPanel( this, contextPanel) );
		repack();
	}
       
	//--------------------------------------------------------------------------
	public void switchToEventPanel(JPanel contextPanel, int year, int month, int day){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToEventPanel()" );
		
		setContentPane( new EventPanel( this, contextPanel, year, month, day ) );
		repack();
	}
        
	//--------------------------------------------------------------------------
	public void switchToPeopleSelectPanel(JPanel contextPanel){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToPeopleSelectPanel()" );
		
		setContentPane( new PeopleSelectPanel( this, contextPanel ) );
		repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToPeoplePanel(JPanel contextPanel){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToPeoplePanel()" );
		
		setContentPane( new PeoplePanel( this, contextPanel ) );
		repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToPersonInfoPanel(JPanel contextPanel, Person person ){
		//switches entire frame to PersonInfoPanel
		debugOutput( "switchToPersonInfoPanel(" + person + ")" );
		
		setContentPane( new PersonInfoPanel( this, contextPanel, person ) );
		repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToEditPersonPanel(JPanel contextPanel, Person person){
		//switches entire frame to EditPersonPanel
		debugOutput( "switchToEditPersonPanel()" );
		
		setContentPane( new EditPersonPanel( this, contextPanel, person ) );
		repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToAddPersonPanel(JPanel contextPanel){
		//switches entire frame to AddPersonPanel
		debugOutput( "switchToAddPersonPanel()" );
		
		setContentPane( new AddPersonPanel( this, contextPanel ) );
		repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToChoresPanel(JPanel contextPanel){
		//switches entire frame to DivvyChoreCore
		debugOutput( "switchToChoresPanel()" );
		
		setContentPane( new ChoresPanel( this, contextPanel ) );
		repack();
	}
	
	//--------------------------------------------------------------------------
	public void switchToChoreAddPanel(JPanel contextPanel){
		//switches entire frame to DivvyChoreCore
		debugOutput( "switchToChoreAddPanel()" );
		
		setContentPane( new ChoreAddPanel( this, contextPanel ) );
		repack();
	}

   //--------------------------------------------------------------------------
	public void switchToChoreSelectPanel(JPanel contextPanel, String action){
		//switches entire frame to DivvyChoreCore
		debugOutput( "switchToChoreAddPanel()" );

		setContentPane( new ChoresSelectPanel( this, contextPanel, action ) );
		repack();
	}

    //--------------------------------------------------------------------------
	public void switchToChoreEditPanel(JPanel contextPanel, Chore chore){
		//switches entire frame to DivvyChoreCore
		debugOutput( "switchToChoreAddPanel()" );

		setContentPane( new ChoresEditPanel( this, contextPanel, chore) );
		repack();
	}

	//--------------------------------------------------------------------------
	public void switchToKeyboardPanel( 
			JPanel contextPanel, JTextComponent textComponent ){
		//switches entire frame to PeopleOverviewPanel
		debugOutput( "switchToKeyboardPanel()" );
                
		setContentPane( new TouchKeyboard( this, contextPanel, textComponent ) );
		repack();
	}
        
	//--------------------------------------------------------------------------
	//debugging field and method - set debuggingOn to false to suppress msgs
	private static boolean debuggingOn = true;
	private static void debugOutput( String msg ){
		if( debuggingOn ){
			System.out.println( "debug: " + msg );
		}
	}
        
    public void setContentPane(Container contentPane) {
        super.setContentPane(contentPane);
    }

	//===============================================================MAIN METHOD
  public static void main( String[] args ){
  	debugOutput( "main(" + args + ")" );
  	
		javax.swing.SwingUtilities.invokeLater( 
			new Runnable(){ public void run(){ 
				new TestFrame(); 
			} }
		);
  } 
}
//==============================================================================
