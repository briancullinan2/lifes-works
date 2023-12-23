package CS565.Transactions;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.*;

public class Simulation {
	
	Client[] runningClientThreads;
	
	JFrame frame;
	JPanel jpMain, jpDisplayTemp;
	JTextField jtxtfldCubbies, jtxtfldCubbyVal, jtxtfldClients, jtxtfldTotalTrans, jtxtfldDelay;
	JButton jbtnStart;
	JCheckBox jckboxIsConcurrency;
	
	int iCubbies, iCubbyVal, iClients, iTotalTrans, iDelay, iExpectedSum, iCubbyValCounter, iCommittedCounter, iAbortedCounter;
	ArrayList<Integer> alCubbyVal;
	ArrayList<Client> alClient;
	
	Display display;
	Czar czar;
	
	public static void main(String[] args) {
		Simulation sim = new Simulation();
		sim.go();
	}
	
	public void go() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		jpMain = new JPanel();
		jpMain.setLayout(new BoxLayout(jpMain, BoxLayout.X_AXIS));
		jpMain.add(CreateInputPanel());
		jpDisplayTemp = new JPanel();
		jpDisplayTemp.setPreferredSize(new Dimension(400,200));
		jpDisplayTemp.setBorder(new javax.swing.border.TitledBorder("Display"));
		jpDisplayTemp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		display = new Display();
		jpDisplayTemp.add(display);
		
		frame.getContentPane().add(jpMain);
		frame.getContentPane().add(jpDisplayTemp);
		frame.setSize(700, 285);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	private JPanel CreateInputPanel() {
		JPanel jpInput = new JPanel();
		jpInput.setLayout(new BoxLayout(jpInput, BoxLayout.Y_AXIS));
		jpInput.setBorder(new javax.swing.border.TitledBorder("User Input"));
		
		alCubbyVal = new ArrayList<Integer>();
		
		jtxtfldCubbies = new JTextField(5);
		jtxtfldCubbyVal = new JTextField(5);
		jtxtfldClients = new JTextField(5);
		jtxtfldTotalTrans = new JTextField(5);
		jtxtfldDelay = new JTextField(5);
		jckboxIsConcurrency = new JCheckBox();
		jckboxIsConcurrency.setSelected(true);
		jbtnStart = new JButton("Start");
		jbtnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ActionPerformedStart(e);
			}
		});
		
		JPanel row1 = new JPanel();
		row1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel row2 = new JPanel();
		row2.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel row3 = new JPanel();
		row3.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel row4 = new JPanel();
		row4.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel row5 = new JPanel();
		row5.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel row6 = new JPanel();
		JPanel row7 = new JPanel();
		
		row1.add(new JLabel("Number of Cubbies"));
		row1.add(jtxtfldCubbies);
		row2.add(new JLabel("Cubby Value"));
		row2.add(jtxtfldCubbyVal);
		row3.add(new JLabel("Number of Clients"));
		row3.add(jtxtfldClients);
		row4.add(new JLabel("Total Number of Transactions"));
		row4.add(jtxtfldTotalTrans);
		row5.add(new JLabel("Delay"));
		row5.add(jtxtfldDelay);
		row6.add(jckboxIsConcurrency);
		row6.add(new JLabel(" Turn On Concurrency?"));
		row7.add(jbtnStart);
		
		jpInput.add(row1);
		jpInput.add(row2);
		jpInput.add(row3);
		jpInput.add(row4);
		jpInput.add(row5);
		jpInput.add(row6);
		jpInput.add(row7);
		
		return jpInput;
	}
	
	public void ActionPerformedStart(ActionEvent e) {
		String tsCubbies = jtxtfldCubbies.getText();
		String tsCubbyVal = jtxtfldCubbyVal.getText();
		String tsClients = jtxtfldClients.getText();
		String tsTotalTrans = jtxtfldTotalTrans.getText();
		String tsDelay = jtxtfldDelay.getText();
		
		if(tsCubbies.trim().length() == 0 |
		   tsCubbyVal.trim().length() == 0 |
		   tsClients.trim().length() == 0 |
		   tsTotalTrans.trim().length() == 0) {
			JOptionPane.showMessageDialog(frame, "You have an empty field. Please fill out all fields.", "Error", JOptionPane.WARNING_MESSAGE);
		}
		else if(isInteger(tsCubbies) == false |
		   isInteger(tsCubbyVal) == false |
		   isInteger(tsClients) == false |
		   isInteger(tsTotalTrans) == false |
		   isInteger(tsDelay) == false) {
			JOptionPane.showMessageDialog(frame, "One or more fields are not in integer format.", "Error", JOptionPane.WARNING_MESSAGE);
		}
		else {
			iCubbies = Integer.parseInt(tsCubbies);
			iCubbyVal = Integer.parseInt(tsCubbyVal);
			iClients = Integer.parseInt(tsClients);
			iTotalTrans = Integer.parseInt(tsTotalTrans);
			
			iDelay = Integer.parseInt(tsDelay);
			iExpectedSum = 0;
			
			CreateArrayListandExpectedSum(iCubbies);
			if(jckboxIsConcurrency.isSelected()) {
				czar = new Czar(true, Integer.parseInt(tsCubbies), alCubbyVal);
			}
			else {
				czar = new Czar(false, Integer.parseInt(tsCubbies), alCubbyVal);
			}
			
			display.NewJTable(iCubbies, iCubbyVal);
			display.updateValues();

			updateDisplay();
			doSimulation(iDelay);
		}
		
	}
	
	private boolean isInteger(String pstInput) {
		try {
			int temp = Integer.parseInt(pstInput);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
	private void CreateArrayListandExpectedSum(int iCubbies) {
		for(int i=0; i<iCubbies; i++) {
			alCubbyVal.add(iCubbyVal);
			iExpectedSum += iCubbyVal;
		}
	}
	private void doSimulation(int delay) {

		ExecutorService executor = Executors.newFixedThreadPool(iClients);
		
		for(int i=0; i<iTotalTrans; ++i) {
			executor.submit(new Client(czar, delay));
		}
		
		executor.shutdown();
		while(!executor.isTerminated()) {
		}
		
		try{ Thread.sleep(1000); } catch(Exception e) { return; }
		updateDisplay();
	}

	private void updateDisplay() {
		display.iExpectedSum = iExpectedSum;
		display.iAbortedTrans = czar.getNumAborts();
		display.iActualSum = czar.getCurrentSum();
		
		ArrayList<CubbyHole> alCubbyHoles = czar.getCubbies();
		for(int i=0; i<alCubbyHoles.size(); i++) {
			display.jtblCubby.setValueAt(alCubbyHoles.get(i).getVal(), i, 1);
		}
		
		System.out.println("Flag 1: updateDisplay() is called");
		System.out.println("Value of ActualSum: "+czar.getCurrentSum());
		
		display.updateValues();
		frame.repaint();
		
//		SwingUtilities.invokeLater(
//		new Runnable() {
//			public void run() {
//				display.update();
//				frame.invalidate();
//				frame.validate();
//				frame.update(frame.getGraphics());
//			}
//		});
	}
}
