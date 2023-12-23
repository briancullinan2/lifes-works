package CS565.Transactions;

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Display extends JPanel {
	
	int initialSum = 0;
	int iExpectedSum, iActualSum, iAbortedTrans;
	JLabel jlblExpectedSum, jlblActualSum, jlblAbortedTrans;
	JTable jtblCubby;
	JPanel jpTable;
	//Object[][] daObjectData;
	JScrollPane jspTable;
	
	public Display() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		JPanel jpRight = new JPanel();
		jpRight.setLayout(new BoxLayout(jpRight, BoxLayout.Y_AXIS));
		
		initializeForm();
		
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JPanel row3 = new JPanel();
		row1.setLayout(new FlowLayout(FlowLayout.LEFT));
		row2.setLayout(new FlowLayout(FlowLayout.LEFT));
		row3.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		row1.add(new JLabel("Aborted Transactions:"));
		row1.add(jlblAbortedTrans);
		row2.add(new JLabel("Expected Sum:"));
		row2.add(jlblExpectedSum);
		row3.add(new JLabel("Actual Sum:"));
		row3.add(jlblActualSum);
		
		jpRight.add(row1);
		jpRight.add(row2);
		jpRight.add(row3);
		jpRight.add(Box.createVerticalStrut(75));

//		jpTable = CreateJTableCubby(daObjectData);
//		System.out.println(jtblCubby.getRowCount());
		jspTable = new JScrollPane(jtblCubby);
		jspTable.setPreferredSize(new Dimension(200,150));
		jpTable.add(jspTable);
		
		this.add(jpRight);
		this.add(jpTable);
	}
	
	private void initializeForm() {
		iExpectedSum = initialSum;
		iActualSum = initialSum;
		iAbortedTrans = initialSum;
		//daObjectData = new Object[0][0];
		jlblExpectedSum = new JLabel(""+iExpectedSum);
		jlblActualSum = new JLabel(""+iActualSum);
		jlblAbortedTrans = new JLabel(""+iAbortedTrans);
		jtblCubby = CreateJTableCubby();
	}
	
	protected JTable CreateJTableCubby() {
		jpTable = new JPanel();
		Object[][] daObjectData = new Object[0][0];
		String[] saColumnNames = {"Cubbies","Value"};
		jtblCubby = new JTable(daObjectData, saColumnNames);
		jtblCubby.getTableHeader().setReorderingAllowed(false);
		return jtblCubby;
	}
	
	protected void NewJTable(int piCubbies, int piCubbyVal) {
		jtblCubby = new JTable(0,0);
		Object[][] pdaObjectData = new Object[piCubbies][2];
		for(int i=0; i<piCubbies; i++) {
			pdaObjectData[i][0] = "Cubby "+(i+1);
			pdaObjectData[i][1] = piCubbyVal;
			System.out.println(pdaObjectData[i][0]+", "+pdaObjectData[i][1]);
		}
		String[] saColumnNames = {"Cubbies","Value"};
		jtblCubby = new JTable(pdaObjectData, saColumnNames);
		//jtblCubby = tjtblCubby;
		jspTable.removeAll();
		jspTable = new JScrollPane(jtblCubby);
		jspTable.setPreferredSize(new Dimension(200,150));
		jpTable.removeAll();
		jpTable.add(jspTable);
		jpTable.revalidate();
	}
	
	public void updateValues() {
		jlblAbortedTrans.setText(iAbortedTrans+"");
		jlblExpectedSum.setText(iExpectedSum+"");
		jlblActualSum.setText(iActualSum+"");
		
		this.repaint();
		
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				Display.this.invalidate();
//				Display.this.validate();
//				Display.this.update(Display.this.getGraphics());
//			}
//		});
	}
}
