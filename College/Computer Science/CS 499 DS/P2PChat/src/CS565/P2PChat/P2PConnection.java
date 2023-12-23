/**
 * File: P2PCore.java
 * Authors:
 *   Andy Arminio
 *   Brian Cullinan
 *   Davis Zanot
 *   Ryan Middleton
 *   Talbert Tso
 *
 * Comments:
 *
 **/
package CS565.P2PChat;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class P2PConnection {

	private Socket peer;
	private ObjectOutputStream out;
	private PeerListener listener;
	private P2PCore core;
        private P2PConnection outerClass;

	/**
	 *
	 */
	public P2PConnection(Socket peer, P2PCore core) throws Exception {

                outerClass = this;
                this.peer = peer;
		this.core = core;

		out = new ObjectOutputStream(peer.getOutputStream());

		//launch listener thread
		listener = new PeerListener();
		listener.start();
	}

	public void sendMessage(Message m) throws Exception {
		out.writeObject(m);
	}

	public void close() {
            try {
				peer.close();
            } catch (Exception ex) {
				System.out.println("Socket closed forcefully.");
            }
	}

        public String getRemoteIP()
        {
            return peer.getRemoteSocketAddress().toString();
        }


	/**
	 *
	 *
	 */
	private class PeerListener extends Thread {

		private ObjectInputStream in;

		public void run()
		{
			Message message = null;
			P2PConnection parent;
			ArrayList<P2PConnection> children;

			try
			{
				in = new ObjectInputStream(peer.getInputStream());
				while(true) {
					message = (Message)in.readObject();
					message.update(core);

					//forward message to all other connections
                                        if(message.doForward())
                                        {
                                            parent = core.getParent();
                                            children = core.getChildren();
                                            if(parent != null) {
                                                    if(parent.listener != this) parent.sendMessage(message);
                                            }
                                            for(P2PConnection p: children)
                                            {
                                                    if(p.listener != this) p.sendMessage(message);
                                            }
                                        }
				}

			} catch (SocketException e) {
				System.out.println(e.getMessage());
                        } catch (EOFException e) {
                            if(core.getParent() != null) {
                                if(core.getParent() == outerClass) core.clearParent();
                            }
                            for(P2PConnection p: core.getChildren()) {
                                if(p == outerClass) core.getChildren().remove(p);
                            }
                        } catch (IOException e) {
				System.err.println("Error with connection to peer");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("Unkown object sent from peer");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
