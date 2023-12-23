package CS565.P2PChat;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class P2PChat extends JFrame {
	private P2PCore core;
        private JList userList;
	
        final static int SPACER = 10;
        private String myName = "Bob";
        private JTextArea chatWindow = new JTextArea("", 25, 0);
	private JTextField inputBox = new JTextField();

	public P2PChat() {
		core = new P2PCore(this);
                buildGUI();
	}

	private void buildGUI() {
		setupWindow();
		buildChatInterface();
                buildMenu();
		chatWindow.setText("Welcome to P2P chat. Use the connect " +
                    "menu item to connect to a peer.");
	}

	private void buildChatInterface() {
		Box vWrapper = Box.createVerticalBox();
		vWrapper.add(Box.createVerticalStrut(SPACER));
		vWrapper.add(setChatWindow());
		vWrapper.add(Box.createVerticalStrut(SPACER));
		vWrapper.add(setChatInput());
		vWrapper.add(Box.createVerticalStrut(SPACER));
		
		Box hWrapper = Box.createHorizontalBox();
		hWrapper.add(Box.createHorizontalStrut(SPACER));
		hWrapper.add(vWrapper);
		hWrapper.add(Box.createHorizontalStrut(SPACER));
                hWrapper.add(setChatUsers());
                hWrapper.add(Box.createHorizontalStrut(SPACER));

		add(hWrapper);
	}

	private void setupWindow() {
		setSize(500, 500);
		setTitle("Amazing P2P Chat Program of Awesomeness!");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {

			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}

			public void windowClosing(WindowEvent arg0) {
				disconnect();
			}
						
		});
		
		getContentPane().setLayout(new BorderLayout());
	}
	
	private void buildMenu() {
		MenuBar menuBar = new MenuBar();
		Menu connection = new Menu("Connection");

                MenuItem username = new MenuItem("Change Alias");
                username.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        myName = JOptionPane.showInputDialog("Enter New Alias");
                        core.setUserName(myName);
                        userList.setListData(core.getUserList().toArray());
                    }
                });
                connection.add(username);

		MenuItem connect = new MenuItem("Connect");
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				connect();				
			}
			
		});
		connection.add(connect);

		MenuItem disconnect = new MenuItem("Disconnect");
		disconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
				
			}
			
		});
		connection.add(disconnect);

		menuBar.add(connection);
		setMenuBar(menuBar);
	}

        private Component setChatUsers() {
            JScrollPane userPane;

            core.setUserName(myName);

            userList = new JList(core.getUserList().toArray());
            userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            userList.setLayoutOrientation(JList.VERTICAL_WRAP);
            userList.setPreferredSize(new Dimension(100, 220));

            userPane = new JScrollPane(userList);
            userPane.setPreferredSize(new Dimension(120, 2500));
            
            return userPane;
        }

	private Component setChatInput() {
		Box wrapper = Box.createHorizontalBox();
		wrapper.add(inputBox);
		wrapper.add(Box.createHorizontalStrut(SPACER));
		
		JButton send = new JButton("Send");
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sendMessage();
			}
			
		});
		wrapper.add(send);
		
		return wrapper;
	}

        private Component setChatWindow() {
		chatWindow.setEditable(false);
		chatWindow.setLineWrap(true);
		chatWindow.setWrapStyleWord(true);
		return new JScrollPane(chatWindow);
	}

        protected void addUser(String name) {
            userList.setListData(core.getUserList().toArray());
        }

        protected void removeUser(String name) {
            userList.setListData(core.getUserList().toArray());
        }

       	public void addMessage(String user, String text) {
		appendLine(user + ": " + text);
	}

	protected void disconnect() {
		try {
			core.disconnect();
		} catch (Exception e) {
			appendLine("Something odd happened while I was " +
                            "trying to disconnect...");
			e.printStackTrace();
		}

		appendLine("I'm gone man...");
		System.exit(0);
	}

	protected void connect() {
		String host = inputBox.getText();
		if(host.isEmpty()) {
			appendLine("Please enter the IP of the peer you " +
                            "would like to connect to in the chat box and " +
                            "try again.");
			return;
		}

		try {
			core.connect(host);
		} catch (Exception e) {
			appendLine("Unable to connect to that peer. :(");
			e.printStackTrace();
		}

		inputBox.setText("");
	}

	protected void sendMessage() {
		String text = inputBox.getText();
		try {
			core.sendChatMessageAll(text);
			addMessage(myName, text);
		} catch (Exception e) {
			appendLine("Error sending: \"" + text + "\"");
			e.printStackTrace();
		}
		
		inputBox.setText("");
	}

	public void appendLine(String text) {
		text = chatWindow.getText() + "\n\n" + text;
		chatWindow.setText(text);
	}

	public static void main(String[] args) {
		P2PChat app = new P2PChat();
		app.setVisible(true);
	}
}
