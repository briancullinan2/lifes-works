package ishare.Resources;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class IconHashMap extends HashMap< String, ImageIcon > {
	
	//TODO: currently hardcoded: couldn't find a way to cycle through resources
	private final String[] iconFilelist = {
  		"boot64.png",
  		"calendar64.png",
  		"car64.png",
  		"computer64.png",
  		"date64.png",
  		"game64.png",
  		"tivo64.png",
  		"tv64.png",
  		"unlocked64.png",
			"userBlue64.png",
			"userCyan64.png",
			"userGreen64.png",
			"userPurple64.png",
			"userYellow64.png",
			"userRed64.png",
                        "blueGlass.png"
	};
	
	//--------------------------------------------------------------------------
	public IconHashMap(){
		debugOutput( "new IconHashMap" );
		loadAllPNGsToImageIcons();
	}

	//--------------------------------------------------------------------------
	public void loadAllPNGsToImageIcons(){
		debugOutput( "IconHashMap: loadAllPNGsToImageIcons()" );
		
		for( String filename : iconFilelist ){
			
			debugOutput( "loading icon file = " + filename );
			put( filename, createImageIcon( filename ) );
		}
	}
	
	//--------------------------------------------------------------------------
	public ImageIcon createImageIcon( String path ){
            debugOutput( "IconHashMap: createImageIcon(" + path + ")" );

            java.net.URL imgURL = ishare.Resources.IconHashMap.class.getResource(path);
            if( imgURL == null ){
                System.err.println( "Couldn't find file: " + path );
                return null;
            }
            return new ImageIcon( imgURL );
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
