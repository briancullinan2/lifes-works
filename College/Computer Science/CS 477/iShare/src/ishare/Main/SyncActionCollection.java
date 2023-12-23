//==============================================================================
package ishare.Main;

import ishare.Resources.IconHashMap;
import java.util.ArrayList;
import java.util.HashMap;


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
public class SyncActionCollection extends HashMap< Long, SyncAction > {

	public static final int MAX_ACTIONS = 6;
	
	//--------------------------------------------------------------------------
	public SyncActionCollection(){
		debugOutput( "new PersonCollection" );
		
	}
	
//	//--------------------------------------------------------------------------
//	TODO:
//	public PersonCollection( String saveCollectionFilename ){
//		debugOutput( "new PersonCollection" );
//		setUpIconLists( initialUserIconFilenameSet );
//		loadCollection( saveCollectionFilename );
//	}
	
	//--------------------------------------------------------------------------
	public SyncActionCollection( 
		IconHashMap iconHashMap, String[] initialUserIconFilenameSet ){
		debugOutput( "new SyncActionCollection" );
	}
	
	//--------------------------------------------------------------------------
	public boolean isFull(){
		debugOutput( "SyncActionCollection: isFull()" );
		
		return ( ( size() == MAX_ACTIONS )?( true ):( false ) );
	}
	
	//--------------------------------------------------------------------------
	public void addAction( SyncAction action ){
		debugOutput( "SyncActionCollection: SyncAction("+ action + ")" );
		

		//place into the map with the name as key, 
		//	and remove this persons icon from the available icon list
		//	(if it was in there in the first place)
		put( action.getTime(), action );
		
	}
	
	//--------------------------------------------------------------------------
	public SyncAction findByTime( long time ){
		debugOutput( "SyncActionCollection: findByName(" + time + ")" );
		
		return get( time );
	}
	
	//--------------------------------------------------------------------------
	public ArrayList< SyncAction > findByType( Object obj ){
		debugOutput( "SyncActionCollection: findByType(" + obj + ")" );
		
                ArrayList< SyncAction > returnval = new ArrayList< SyncAction >();
                
		for( SyncAction action : this.values() ){
			if( action.type != SyncAction.Type.IGNORE && action.obj.getClass() == obj.getClass() ){
                            returnval.add(action);
                        }
                }
                
		return returnval;
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
