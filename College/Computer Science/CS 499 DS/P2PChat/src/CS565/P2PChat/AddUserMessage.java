
package CS565.P2PChat;

import java.io.*;


public class AddUserMessage extends CmdMessage implements Serializable {

	private String user;
	public AddUserMessage(String user) {
		this.user = user;
	}
	public void update(P2PCore core){
		core.addUser(user);
	}

}