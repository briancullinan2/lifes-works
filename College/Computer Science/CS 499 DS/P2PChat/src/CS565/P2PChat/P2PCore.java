package CS565.P2PChat;

import java.util.ArrayList;
import java.io.IOException;
import java.net.*;

/**
 * P2PCore manages all of the background tasks for a P2P
 * chat program.  It listens for connecting peers using a
 * separate thread of execution.  Each resulting socket is
 * wrapped in a {@link <P2PConnection> [P2PConnection]} object
 * which each spawn their own separate thread for receiving
 * {@link <Message> [Message]} objects over their respective
 * socket.
 *
 * @author Andy Arminio
 * @author Brian Cullinan
 * @author Davis Zanot
 * @author Ryan Middleton
 * @author Talbert Tso
 */
public class P2PCore {

    	private volatile P2PChat ui;
    	private volatile P2PCore self;
        private volatile P2PServer server;

       	private volatile ArrayList<String> users;
	private volatile ArrayList<P2PConnection> children;
	private volatile P2PConnection parent;
	private String userName;
	private int port;

	/**
	 * @param ui User interface reference
	 */
	public P2PCore(P2PChat ui) {
		this(ui, 1234);
	}

	/**
	 * @param ui User interface reference
         * @param port The port to listen for connecting peers
	 */
	public P2PCore(P2PChat ui, int port) {

      		this.ui = ui;
		this.self = this;

       		this.users = new ArrayList<String>();
		this.children = new ArrayList<P2PConnection>();
		this.port = port;

                this.userName = "test";
                this.users.add(userName);

		//begin listening for connecting peers
		server = new P2PServer(port);
		server.start();
	}

	/**
	 *
	 */
	public ArrayList<P2PConnection> getChildren() {
            return children;
        }

	/**
	 *
	 */

	public void addUser(String user) {
		if(this.users.indexOf(user) < 0) {
			this.users.add(user);
		}
                ui.addUser(user);
	}
	/**
	 *
	 */
	public void addUserList(ArrayList<String> users) {
		for(String user : users) {
			addUser(user);
		}
	}

	/**
	 *
	 */
	public void removeUser(String user) {
		if(this.users.indexOf(user) >= 0) {
			this.users.remove(this.users.indexOf(user));
		}
                ui.removeUser(user);
	}

        /**
         *
         */
        public ArrayList<String> getUserList()
        {
            return users;
        }

	/**
	 *
	 */
	public P2PConnection getParent() { return parent; }

        /**
         *
         */
        public void clearParent() { parent = null; }

	/**
	*
	*/
	public void setUserName(String user) {
		this.users.remove(this.users.indexOf(this.userName));
		this.userName = user;
		this.users.add(this.userName);

	}

	/**
	 *
	 */
	public void connect(String IP) throws Exception {
            if(parent != null)
                parent.close();
            if(IP.equals("")){
                parent = null;
            } else {
                Socket parentsocket = new Socket(IP, this.port);
		parent = new P2PConnection(parentsocket, this);
            }
            UserListMessage addUsers = new UserListMessage(this.users);
            parent.sendMessage(addUsers);
	}

	/**
	 *
	 */
	public void disconnect() throws Exception {

            RemoveUserMessage remove = new RemoveUserMessage(this.userName);

            //Reconnect all children to parent
            if(parent != null) {
                LeaveMessage leave = new LeaveMessage(parent.getRemoteIP(), server.getLocalAddress());

                parent.sendMessage(remove);
                parent.close();

                for(P2PConnection p: children) {
                    p.sendMessage(remove);
                    p.sendMessage(leave);
                    p.close();
                }
            } else {
                if(children.size() == 0) {
                    //nothing to do...
                } else if(children.size() == 1) {
                    LeaveMessage leave = new LeaveMessage("", server.getLocalAddress());
                    P2PConnection p = children.get(0);

                    p.sendMessage(remove);
                    p.sendMessage(leave);
                    p.close();
                } else {
                    P2PConnection p0 = children.get(0);

                    LeaveMessage newRoot = new LeaveMessage("", server.getLocalAddress());
                    LeaveMessage leave   = new LeaveMessage(p0.getRemoteIP(), server.getLocalAddress());

                    //Make first child the new root
                    p0.sendMessage(remove);
                    p0.sendMessage(newRoot);
                    p0.close();

                    //reconnect other children to new root
                    for(int i=1; i<children.size(); ++i) {
                        P2PConnection tmp = children.get(i);
                        tmp.sendMessage(remove);
                        tmp.sendMessage(leave);
                        tmp.close();
                    }
                }
            }
            server.end();
	}

	public void addMessage(String user, String text) {
		ui.addMessage(user, text);
	}

	/**
	 * Sends a message to all connected peers
	 * @param user Name of the user sending the message
	 * @param text Text of the message
	 */
	public void sendChatMessageAll(String text) throws Exception {

		ChatMessage message = new ChatMessage(userName, text);
		if(parent != null) parent.sendMessage(message);
		for(P2PConnection con : children) {
			con.sendMessage(message);
		}
	}

	/**
	 * Sends a message to add a user to all connected peers
	 * @param user Name of the user being added
	 */
	public void sendAddUserMessageAll(String user) throws Exception {

		AddUserMessage message = new AddUserMessage(user);
		if(parent != null) parent.sendMessage(message);
		for(P2PConnection con : children) {
			con.sendMessage(message);
		}
	}


	/**
	 * Inner class to listen for connecting peers as a separate
	 * thread of execution.
	 */
	public class P2PServer extends Thread {

		private int port;
		private boolean isRunning = false;
		ServerSocket server;

        	/**
         	 *
         	 */
		public P2PServer(int port) {
			this.port = port;
		}

                public String getLocalAddress()
                {
                    return server.getLocalSocketAddress().toString();
                }

		/**
		 *
		 */
		public void run() {
			try {
				server = new ServerSocket(port);
				isRunning = true;
				while(isRunning) {
                                    P2PConnection child = new P2PConnection(server.accept(), self);
                                    children.add(child);
                                    UserListMessage userList = new UserListMessage(users);
                                    child.sendMessage(userList);
                                    System.out.println("Child connection saved");
				}

                        } catch (java.net.SocketException ex) {
                            if(ex.getMessage().equals("socket closed"))
                                System.out.println("Server socket closed");
                            else
                                ex.printStackTrace();
			} catch(IOException ex) {
				System.err.println("Error listening on port: " + port);
				ex.printStackTrace();
				return;
			} catch(Exception e) {
				e.printStackTrace();
				return;
			}

		}

		/**
		 *
		 */
		public synchronized void end() throws Exception {
			isRunning = false;
			server.close();
		}
	}
}
