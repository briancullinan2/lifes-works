package CS565.Transactions;
import java.util.ArrayList;

public class Test
{
  public static void main(String args[])
  {
    ArrayList<Client> clients = new ArrayList<Client>();
    ArrayList<Integer> initValues = new ArrayList<Integer>();
    boolean doOcc = false;
    
    if(args.length>0) doOcc = args[0].equals("true");
  
    initValues.add(new Integer(1));
    initValues.add(new Integer(2));
    initValues.add(new Integer(6));
    initValues.add(new Integer(3));
    initValues.add(new Integer(12));
    initValues.add(new Integer(4));
    initValues.add(new Integer(135));
    initValues.add(new Integer(1));
    initValues.add(new Integer(1));
    initValues.add(new Integer(1));
  
    Czar czar = new Czar(doOcc, 10, initValues);
  
    int sum = 0;
    System.out.print("Cubbies: ");
    for(Integer i: initValues)
    {
      System.out.print(i + ",");
    }
    System.out.println("\nInitial sum: " + czar.getCurrentSum());
  
    for(int i=0; i<10; ++i)
    {
      clients.add(new Client(czar));
    }
  
    for(Client c: clients)
    {
      c.start();
    }  
    
    try{ Thread.sleep(100); } catch(Exception e) { return; }
    
    for(int i=0; i<10; ++i)
    {
      System.out.println("" + clients.get(i).getIsDone() + "," + clients.get(i).getPassed());
    }
    ArrayList<CubbyHole> cubbies = czar.getCubbies();
    System.out.print("Cubbies: ");
    for(CubbyHole c: cubbies)
    {
      System.out.print(c.getVal() + ",");
    }
    System.out.println("\nFinal sum: " + czar.getCurrentSum());
  }
}
