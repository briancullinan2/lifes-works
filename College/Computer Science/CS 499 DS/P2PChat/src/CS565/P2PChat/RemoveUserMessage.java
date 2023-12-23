
package CS565.P2PChat;

import java.io.*;


public class RemoveUserMessage extends CmdMessage implements Serializable {

	private String user;
	public RemoveUserMessage(String user) {
		this.user = user;
	}
	public void update(P2PCore core){
		core.removeUser(user);
	}

}