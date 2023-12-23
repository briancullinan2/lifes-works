/**
 This class creates a customer who is wanting a haircut.  Once the haircut
 is received the customer then has finished their task and they have to 
 be re-instantiated if they want another haircut.
*/
import java.util.*;

public class Customer implements Runnable
{
    private BarberShop shop;
    private int customer;
    private int HAIRCUT_TIME = 5;
    
    /**
     * Construct a new customer with their unique identity and give them
     * a barber shop to enter for their haircut.
     * @param pCustomer This customer's identifier.
     * @param pShop The barber shop they will get a haircut in.
    */
    public Customer(int pCustomer, BarberShop pShop)
    {
      //please fill in...
    }
    
    /**
     * The run method for the customer thread tries to get the customer a
     * haircut.  
    */
    public void run()
    {
        int sleeptime = (int) (HAIRCUT_TIME * Math.random() );

        System.out.println("ENTERING SHOP: Customer [" + customer + "] entering barber shop for haircut.");
        int  test = BarberShop.OCUPIED;
            
        //test for this customer's haircut posibility
      
      //please fill in...
      
                   
        //if the barber was busy, then this customer has waited in the waiting room.
        //This waiting customer will now get the next haircut.  Entering threads will
        //have to wait for the existing customers to be serviced.  As far as which
        //waiting thread will get serviced next is up to the JVM.
      //please fill in...
      
            
        //otherwise, no one in barber shop, wake up barber and get haircut
      //please fill in...
            
        //this barber shop is full.  Therefore leave and never return.
        else if (test == BarberShop.FULL)
        {
            System.out.println("Barber Shop full: Customer [" + customer + "] is leaving shop.");
            return;
        }
            
        //Barber's ready to take this customer right away for a haircut.
        else
            System.out.println("HAIRCUT: Customer [" + customer + "] is getting haircut.");               
                
        //customer is now getting their haircut for an amount of time
        SleepUtilities.nap();

        //haircut finished.  Leave the shop and notify anyone waiting.
        System.out.println("LEAVING SHOP: Customer [" + customer + "] haircut finished: leaving shop.");
        shop.leaveBarberShop(customer);
    }
}
