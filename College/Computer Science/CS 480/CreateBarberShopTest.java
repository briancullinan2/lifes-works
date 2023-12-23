/**
 *CreateBarberShopTest.java
 
 Test Stub for the BarberShop\Customer problem.  This class adds customers
 to a constructed barber shop indefinately.
*/

public class CreateBarberShopTest implements Runnable
{
    static final private int WAIT_TIME = 3;
    
    static public void main(String [] args)
    {
        new Thread(new CreateBarberShopTest()).start();
    }
    
    public void run()
    { 
        //create the barber shop
        BarberShop newShop = new BarberShop(15);
        int customerID = 1;
        
        //add the specifid number of threads to shop
        while(customerID <= 10000)
        {
            //add customers to the barber shop
            new Thread(new Customer(customerID, newShop)).start();
            customerID++;

            //wait this amount of time before adding another customer to shop
            SleepUtilities.nap();
        }      
    }
}
