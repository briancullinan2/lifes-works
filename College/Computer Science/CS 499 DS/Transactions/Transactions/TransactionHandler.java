
/**
 *
 *
 * @author Davis Zanot
 */
package CS565.Transactions;

import java.util.ArrayList;

public class TransactionHandler {
	private Czar czar;
	private Transaction transaction;


	public TransactionHandler(Czar czar, Transaction t)
	{
		this.czar = czar;
		transaction = t;
	}

	public TransactionHandler(Czar czar) {
		this.czar = czar;
		transaction = new Transaction();
	}
	public int getNumCubbies() {
		return czar.getCubbies().size();
	}

	public int readValue(int cubby) {
		if(transaction.getStartTime() == -1) {
			transaction.setStartTime( System.currentTimeMillis() );
		}
		int value = czar.getCubbies().get(cubby).getVal();
		transaction.readCubbies.add(cubby);
		return value;
	}

	public void writeValue(int value, int cubby) {
		if(transaction.getStartTime() == -1) {
			transaction.setStartTime( System.currentTimeMillis() );
		}
		transaction.writtenCubbies.add(cubby);
		transaction.writtenValues.add(value);
	}
	public Transaction getTransaction() {
		return transaction;
	}
	public boolean commit() {
		transaction.setCommitTime( System.currentTimeMillis() );
		return czar.commit(this);
	}
}