
/**
 * @author Davis Zanot
 */

package CS565.Transactions;

import java.util.ArrayList;

public class Transaction {
	public long startTime, commitTime, startWrite, endWrite;
	public ArrayList<Integer> readCubbies, writtenCubbies, writtenValues;

	public Transaction() {
		startTime  = -1;
		commitTime = -1;
		startWrite = -1;
		endWrite   = -1;

		readCubbies  =  new ArrayList<Integer>();
		writtenCubbies  =  new ArrayList<Integer>();
		writtenValues  =  new ArrayList<Integer>();
	}

	public long getStartTime() {
		return startTime;
	}

	public long getCommitTime() {
		return commitTime;
	}

	public long getStartWrite() {
		return startWrite;
	}

	public long getEndWrite() {
		return endWrite;
	}

	public ArrayList<Integer> getWrittenCubbies() {
		return writtenCubbies;
	}

	public ArrayList<Integer> getReadCubbies() {
		return readCubbies;
	}

	public ArrayList<Integer> getWrittenValues() {
		return writtenValues;
	}

	public void setStartTime(long i) {
		startTime = i;
	}

	public void setCommitTime(long i) {
		commitTime = i;
	}

	public void setStartWrite(long i) {
		startWrite = i;
	}

	public void setEndWrite(long i) {
		endWrite = i;
	}
}