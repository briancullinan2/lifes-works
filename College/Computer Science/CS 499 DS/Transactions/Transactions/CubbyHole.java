package CS565.Transactions;

public class CubbyHole {
	private int val;

	public CubbyHole(int val) {
		this.val = val;
	}
	
	public synchronized void setVal(int val) {
		this.val = val;		
	}

	public synchronized int getVal() {
		return val;
	}
}
