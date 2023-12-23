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
public class PersonCollection extends HashMap< String, Person > {

	public static final int MAX_PERSONS = 6;

	//where are the icon resources located and what are their filenames?
	String[] initialUserIconFilenameSet = {
  		"userRed64.png",
  		"userGreen64.png",
  		"userBlue64.png",
  		"userCyan64.png",
  		"userYellow64.png",
  		"userPurple64.png"
        };
  
	//lists of all loaded icons and icons currently unused
	ArrayList< ImageIcon > personIcons = null;
	ArrayList< ImageIcon > availableIcons = null;
	
	//--------------------------------------------------------------------------
	public PersonCollection( IconHashMap iconHashMap ){
		debugOutput( "new PersonCollection" );
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
	public PersonCollection( 
			IconHashMap iconHashMap, String[] initialUserIconFilenameSet ){
		debugOutput( "new PersonCollection" );
		setUpIconLists( iconHashMap, initialUserIconFilenameSet );
	}
	
	//--------------------------------------------------------------------------
	private void setUpIconLists( 
		IconHashMap iconHashMap, String[] initialUserIconFilenameSet ){
		debugOutput( "PersonCollection.setUpIconLists()" );
		
		//set up and populate the two icon lists 
		personIcons = new ArrayList< ImageIcon >();
		availableIcons = new ArrayList< ImageIcon >();
		for( String filename : initialUserIconFilenameSet ){
			ImageIcon icon = iconHashMap.get( filename );
			
			if( icon != null ){
				personIcons.add( icon );
				availableIcons.add( icon );
			}
		}
		debugOutput( "PersonCollection: There are currently " 
				+ availableIcons.size() + " icons available for use" );
	}
	
	//--------------------------------------------------------------------------
	public boolean isFull(){
		debugOutput( "PersonCollection: isFull()" );
		
		return ( ( size() == MAX_PERSONS )?( true ):( false ) );
	}
	
	//--------------------------------------------------------------------------
	public void addPerson( Person person ){
		debugOutput( "PersonCollection: addPerson("+ person + ")" );
		

		//place into the map with the name as key, 
		//	and remove this persons icon from the available icon list
		//	(if it was in there in the first place)
		put( person.getName(), person );
		
		if( availableIcons.contains( person.getIcon() ) ){
			availableIcons.remove( person.getIcon() );
			debugOutput( "PersonCollection: " + 
					person.getName() + "'s icon found in availableIcons" );
			debugOutput( "PersonCollection: after removal, availableIcons.size = " 
					+ availableIcons.size() );
		}
	}
	
	//--------------------------------------------------------------------------
	public Person findByName( String name ){
		debugOutput( "PersonCollection: findByName(" + name + ")" );
		
		return get( name );
	}
	
	//--------------------------------------------------------------------------
	public Person findByIcon( ImageIcon icon ){
		debugOutput( "PersonCollection: findByIcon(" + icon + ")" );
		
		//TODO: seems fragile (make sure we aren't loading user icons anywhere else)
		for( Person person : this.values() ){
			if( person.getIcon().equals( icon ) ){
				debugOutput( "PersonCollection:  found icon" );
				return person;
			}
		}
		return null;
	}
	
	//--------------------------------------------------------------------------
	public int getNumberOfAvailableIcons(){
		debugOutput( "PersonCollection: getNumberOfAvailableIcons()" );
		
		return availableIcons.size();
	}
	
	//--------------------------------------------------------------------------
	public ArrayList< ImageIcon > getAvailableIcons(){
		debugOutput( "PersonCollection: getAvailableIcons()" );
		
		return availableIcons;
	}
	
	//--------------------------------------------------------------------------
	public boolean hasAvailableIcon(){
		debugOutput( "PersonCollection: hasAvailableIcon()" );
		
		return ( ( availableIcons.isEmpty() )?( false ):( true ) );
	}
	
	//--------------------------------------------------------------------------
	public ImageIcon getNextAvailableIcon(){
		debugOutput( "PersonCollection: getNextAvailableIcon()" );
		
		return ( ( availableIcons.isEmpty() )?
				( null ):( availableIcons.get( 0 ) ) );
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
