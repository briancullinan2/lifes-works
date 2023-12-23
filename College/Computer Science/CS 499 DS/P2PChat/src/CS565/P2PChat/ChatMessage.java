/**
 *
 *
 *
 **/
package CS565.P2PChat;

import java.io.*;

public class ChatMessage extends Message implements Serializable {

	private String name, text;
	
	public ChatMessage(String name, String text) {
		this.name = name;
		this.text = text;
	}
	
	public synchronized void update(P2PCore core) {
		core.addMessage(name, text);
	}
}
