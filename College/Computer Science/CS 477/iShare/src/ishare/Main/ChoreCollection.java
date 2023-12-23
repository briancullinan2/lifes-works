//==============================================================================
package ishare.Main;

import ishare.Resources.IconHashMap;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;


/*=========================================================================*//**
 * @author Carl Eberhard
 * @version 0.1
 * 
 * A subclass of HashMap that maps a person (user) name to a Person object.
 * Pretty much a convenience class, it adds these fields:
 * 	an MAX_PERSONS static field - for the maximum number of users (set here)
 *  an initial list of possible user icons
 *  a string path of the resource location
 *  an array list holding all the icons loaded from the paths above
 *  an array list holding all those icons not being used by a person
 *  	(good for grabbing a new icon for a new user)
 *  
 * and these methods:
 *  isFull: returns true if this map's size is already MAX_PERSONS
 *  findByName: searches contained Persons for a specific name
 *  findByIcon: searches contained Persons for a specific icon
 *  getNumberOfAvailableIcons, getAvailableIcons, hasAvailableIcon, 
 *  	and getNextAvailableIcon: which should be self explanatory
 *  createImageIcon: a helper function to load icons from in a JAR friendly way
 * 
 *///===========================================================================
public class ChoreCollection extends HashMap< String, Chore > {

	public static final int MAX_PERSONS = 6;

	//where are the icon resources located and what are their filenames?
	String[] initialUserIconFilenameSet = {
  		"boot64.png",
  		"calendar64.png",
  		"car64.png",
  		"computer64.png",
  		"date64.png",
  		"game64.png",
  		"tivo64.png",
  		"tv64.png",
  		"unlocked64.png"
        };
  
	//lists of all loaded icons and icons currently unused
	ArrayList< ImageIcon > choreIcons = null;
	ArrayList< ImageIcon > availableIcons = null;
	
	//--------------------------------------------------------------------------
	public ChoreCollection( IconHashMap iconHashMap ){
		debugOutput( "new ChoreCollection" );
		setUpIconLists( iconHashMap, initialUserIconFilenameSet );
		
	}
	
//	//--------------------------------------------------------------------------
//	TODO:
//	public PersonCollection( String saveCollectionFilename ){
//		debugOutput( "new PersonCollection" );
//		setUpIconLists( initialUserIconFilenameSet );
//		loadCollection( saveCollectionFilename );
//	}
	
	//--------------------------------------------------------------------------
	public ChoreCollection( 
			IconHashMap iconHashMap, String[] initialUserIconFilenameSet ){
		debugOutput( "new ChoreCollection" );
		setUpIconLists( iconHashMap, initialUserIconFilenameSet );
	}
	
	//--------------------------------------------------------------------------
	private void setUpIconLists( 
		IconHashMap iconHashMap, String[] initialUserIconFilenameSet ){
		debugOutput( "ChoreCollection.setUpIconLists()" );
		
		//set up and populate the two icon lists 
		choreIcons = new ArrayList< ImageIcon >();
		availableIcons = new ArrayList< ImageIcon >();
		for( String filename : initialUserIconFilenameSet ){
			ImageIcon icon = iconHashMap.get( filename );
			
			if( icon != null ){
				choreIcons.add( icon );
				availableIcons.add( icon );
			}
		}
		debugOutput( "ChoreCollection: There are currently " 
				+ availableIcons.size() + " icons available for use" );
	}
	
	//--------------------------------------------------------------------------
	public boolean isFull(){
		debugOutput( "ChoreCollection: isFull()" );
		
		return ( ( size() == MAX_PERSONS )?( true ):( false ) );
	}
	
	//--------------------------------------------------------------------------
	public void addChore( Chore chore ){
		debugOutput( "ChoreCollection: addPerson("+ chore + ")" );
		

		//place into the map with the name as key, 
		//	and remove this persons icon from the available icon list
		//	(if it was in there in the first place)
		put( chore.getTitle(), chore );
		
		if( availableIcons.contains( chore.getIcon() ) ){
			availableIcons.remove( chore.getIcon() );
			debugOutput( "PersonCollection: " + 
					chore.getTitle() + "'s icon found in availableIcons" );
			debugOutput( "PersonCollection: after removal, availableIcons.size = " 
					+ availableIcons.size() );
		}
	}

    public Chore remove(Object key) {
        return super.remove(key);
    }

	//--------------------------------------------------------------------------
	public Chore findByTitle( String title ){
		debugOutput( "ChoreCollection: findByName(" + title + ")" );
		
		return get( title );
	}
	
	//--------------------------------------------------------------------------
	public Chore findByIcon( ImageIcon icon ){
		debugOutput( "ChoreCollection: findByIcon(" + icon + ")" );
		
		//TODO: seems fragile (make sure we aren't loading user icons anywhere else)
		for( Chore chore : this.values() ){
			if( chore.getIcon().equals( icon ) ){
				debugOutput( "PersonCollection:  found icon" );
				return chore;
			}
		}
		return null;
	}
	
	//--------------------------------------------------------------------------
	public int getNumberOfAvailableIcons(){
		debugOutput( "ChoreCollection: getNumberOfAvailableIcons()" );
		
		return availableIcons.size();
	}
	
	//--------------------------------------------------------------------------
	public ArrayList< ImageIcon > getAvailableIcons(){
		debugOutput( "ChoreCollection: getAvailableIcons()" );
		
		return availableIcons;
	}
	
	//--------------------------------------------------------------------------
	public boolean hasAvailableIcon(){
		debugOutput( "ChoreCollection: hasAvailableIcon()" );
		
		return ( ( availableIcons.isEmpty() )?( false ):( true ) );
	}
	
	//--------------------------------------------------------------------------
	public ImageIcon getNextAvailableIcon(){
		debugOutput( "ChoreCollection: getNextAvailableIcon()" );
		
		return ( ( availableIcons.isEmpty() )?
				( null ):( availableIcons.get( 0 ) ) );
	}

    // •••••••••• Added for functionality to ChoreRemovePanel
    //--------------------------------------------------------------------------
    public void RemoveChoreByTitle( String title ) {
        remove( title );
    }
    
	
	//--------------------------------------------------------------------------
	//debugging field and method - set debuggingOn to false to suppress msgs
	private static boolean debuggingOn = true;
	private static void debugOutput( String msg ){
		if( debuggingOn ){
			System.out.println( "debug: " + msg );
		}
	}

}
//==============================================================================
