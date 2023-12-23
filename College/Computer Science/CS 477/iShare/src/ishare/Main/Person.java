//==============================================================================
package ishare.Main;

import javax.swing.ImageIcon;

/*=========================================================================*//**
 * @author Carl Eberhard
 * @version 0.1
 * 
 * Represents the Persons (users) of the iShare app.
 * Currently only a (extensible) container of a name and an icon.
 * (The fields and methods should be pretty self explanatory right now)
 * 
 * We can add more to the structure here.
 * 
 *///===========================================================================
public class Person {
	
	private String name = null;					//the user's chosen name
	private ImageIcon icon = null;			//the user's chosen pic/icon
	
	//calendar/reservation collection
	
	//--------------------------------------------------------------------------
	public Person( String name, ImageIcon icon ){
		this.name = name;
		this.icon = icon;
	}

	//--------------------------------------------------------------------------
	public String getName(){ return name; }
	public void setName( String name ){ 
		this.name = name;
	}
	
	//--------------------------------------------------------------------------
	public ImageIcon getIcon(){ return icon; }
	public void setIcon( ImageIcon icon ){
		this.icon = icon;
	}

	//--------------------------------------------------------------------------
	public String toString(){ return ( getName() + icon ); }
	
}
//==============================================================================
