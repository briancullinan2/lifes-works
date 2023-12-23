package CS565.Transactions;
import java.util.ArrayList;


public class Czar {
	private ArrayList<CubbyHole> cubbies;
	private ArrayList<TransactionHandler> transHandlers;
	private ArrayList<Transaction> commitedTransactions;
	private boolean doOcc;
	private int numAborts;

	public Czar(boolean doOcc, int numCubbies, ArrayList<Integer> initValues) {
		cubbies = new ArrayList<CubbyHole>();
		transHandlers = new ArrayList<TransactionHandler>();
		commitedTransactions = new ArrayList<Transaction>();
		this.doOcc = doOcc;
		numAborts = 0;

		for(int i=0; i<numCubbies; ++i)
		{
			cubbies.add(new CubbyHole(initValues.get(i).intValue()));
		}
	}

	public synchronized TransactionHandler connect() {
		TransactionHandler h = new TransactionHandler(this);
		transHandlers.add(h);
		return h;
	}

	public synchronized boolean commit(TransactionHandler tHandler) {
		transHandlers.remove(tHandler);
		Transaction trans = tHandler.getTransaction();
		
		trans.setStartWrite(System.currentTimeMillis());

		//Detect conflicting transactions if enabled
		if(doOcc)
		{
			
			for(Transaction oldTrans : commitedTransactions) {
				if(intersects(trans,oldTrans))
				{
					++numAborts;
					return false;
				}
			}
		}

		commitedTransactions.add(trans);
		writeTransaction(trans);
		trans.setEndWrite(System.currentTimeMillis());
		System.out.flush();
		
		return true;
	}

	private void writeTransaction(Transaction trans) {
		ArrayList<Integer> cubbyIndex = trans.getWrittenCubbies();
		ArrayList<Integer> cubbyValue = trans.getWrittenValues();

		for(int i = 0; i < cubbyIndex.size(); ++i) {
			int index = cubbyIndex.get(i);
			int v = cubbyValue.get(i);
			cubbies.get(index).setVal(v);
			System.out.println("In cubby " + index + " I am putting value " + v);
		}

	}

	private boolean intersects(Transaction t1, Transaction t2) {
		long a = t1.getStartTime();
		long b = t1.getCommitTime();
		long c = t2.getStartWrite();
		long d = t2.getEndWrite();
		if((a <= c && b >= c)||(a >= c && a <= d))
			return (intersects(t1.getReadCubbies(), t2.getWrittenCubbies())
					|| intersects(t1.getWrittenCubbies(), t2.getWrittenCubbies()));
		return false;
	}

	private boolean intersects(ArrayList<Integer> set1, ArrayList<Integer> set2) {
		for(int i1 : set1) {
			for(int i2 : set2) {
				if(i1 == i2) return true;
			}
		}
		return false;
	}
	
	public synchronized ArrayList<CubbyHole> getCubbies() {
		return cubbies;
	}
	
	public synchronized int getNumAborts() {
		return numAborts;
	}
	
	public synchronized int getCurrentSum() {
		int sum = 0;

		for(CubbyHole cubby: cubbies) {
			sum += cubby.getVal();
		}

		return sum;
	}
}
