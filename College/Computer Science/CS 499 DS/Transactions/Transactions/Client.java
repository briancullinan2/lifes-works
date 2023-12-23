package CS565.Transactions;

public class Client extends Thread
{
	private TransactionHandler transHandler = null;
	private boolean passed;
	private boolean isDone;
	private int delayMillis;
	
	public Client(Czar czar)
	{
		passed = false;
		isDone = false;
		transHandler = czar.connect();
		this.delayMillis = 0;
	}

	public Client(Czar czar, int delayMillis)
	{
		passed = false;
		isDone = false;
		transHandler = czar.connect();
		this.delayMillis = delayMillis;
	}

	public boolean getPassed()
	{
		return passed;
	}
	
	public boolean getIsDone()
	{
		return isDone;
	}
	
	public void run()
	{
		try{ Thread.sleep((int)(Math.random()*delayMillis)); } catch(Exception e){ return; }
		
		//Choose cubbies to modify & value to use
		int numCubbies = transHandler.getNumCubbies();
		int index1     = (int)(Math.random()*numCubbies);
		int index2     = (int)(Math.random()*numCubbies);
		int value      = (int)(Math.random()*100)+1;

		while(index1 == index2) index2 = (int)(Math.random()*numCubbies);
		
		//Perform transaction
		transHandler.writeValue(transHandler.readValue(index1)-value, index1);
		transHandler.writeValue(transHandler.readValue(index2)+value, index2);
		passed = transHandler.commit();
		isDone = true;
	}
}