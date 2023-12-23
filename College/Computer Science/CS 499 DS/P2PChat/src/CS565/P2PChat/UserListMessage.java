
package CS565.P2PChat;

import java.io.*;
import java.util.*;


public class UserListMessage extends CmdMessage implements Serializable {

	private ArrayList<String> users;
	public UserListMessage(ArrayList<String> users) {
		this.users = users;
	}
	public void update(P2PCore core){
		core.addUserList(users);
	}

}