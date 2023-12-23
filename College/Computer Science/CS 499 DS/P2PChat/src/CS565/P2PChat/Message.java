/**
 *
 *
 *
 **/
package CS565.P2PChat;

import java.io.*;

public abstract class Message implements Serializable {
	public abstract void update(P2PCore core);
        public boolean doForward() { return true; }
}
